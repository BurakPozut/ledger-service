package com.ledger.ledger_service.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.ledger_service.dto.Reponse.TransferResponse;
import com.ledger.ledger_service.dto.Request.CreateTransferRequest;
import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.entity.Transfer;
import com.ledger.ledger_service.repository.AccountRepository;
import com.ledger.ledger_service.repository.TransferRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@Service
public class TransferService {

  @Autowired
  private TransferRepository transferRepository;

  @Autowired
  private AccountRepository accountRepository;

  // @Autowired
  // private LedgerEntryService ledgerEntryService;
  @Autowired
  private EventPublisherService eventPublisherService;

  @Autowired
  private RetryRegistry retryRegistry;

  private static final String STATUS_PENDING = "PENDING";

  private static final String STATUS_COMPLETED = "COMPLETED";

  private static final String STATUS_FAILED = "FAILED";

  private static final String STATUS_CANCELLED = "CANCELLED";

  private static final String FAILURE_INSUFFICENT_FUNDS = "INSUFFICENT_FUNDS";
  private static final String FAILURE_ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";
  private static final String FAILURE_CURRENCY_MISMATCH = "CURRENCY_MISMATCH";
  private static final String FAILURE_SAME_ACCOUNT = "SAME_ACCOUNT";
  private static final String FAILURE_DUPLICATE_REQUEST = "DUPLICATE_REQUEST";
  private static final String FAILURE_INVALID_AMOUNT = "INVALID_AMOUNT";

  // This is for demonstration purposes normally we need to put circuit breaking
  // when we are making calling other microservices or message queues etc.
  @CircuitBreaker(name = "transferService", fallbackMethod = "createTransferFallback")
  @TimeLimiter(name = "transferService")
  @Transactional
  public TransferResponse createTransfer(CreateTransferRequest request) {
    Retry retry = retryRegistry.retry("transferService");

    Supplier<TransferResponse> decoreatedSupplier = Retry.decorateSupplier(
        retry,
        () -> executeTransfer(request));

    try {
      return decoreatedSupplier.get();
    } catch (Exception e) {
      if (e instanceof OptimisticEntityLockException) {
        throw new TransferException("Trasnfer failed after retries due to concurrent updates",
            "CONCURRENT_UPDATE_FAILED");
      }
      throw e;
    }
  }

  public TransferResponse executeTransfer(CreateTransferRequest request) {
    validateTransferRequest(request);

    // Check for duplicate requests (idempotency)
    Optional<Transfer> existingTransfer = transferRepository
        .findBySourceAccountAccountIdAndClientRequestId(request.getSourceAccountId(), request.getClientRequestId());

    if (existingTransfer.isPresent()) {
      throw new TransferException("Transfer with this client request id already exists", FAILURE_DUPLICATE_REQUEST);
    }

    // Get Accounts
    Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
        .orElseThrow(() -> new TransferException("Source account not found", FAILURE_ACCOUNT_NOT_FOUND));
    Account targetAccount = accountRepository.findById(request.getTargetAccountId())
        .orElseThrow(() -> new TransferException("Target account not found", FAILURE_ACCOUNT_NOT_FOUND));

    validateAccountsAndTransfer(sourceAccount, targetAccount, request);

    Transfer transfer = new Transfer();
    transfer.setTransferId(UUID.randomUUID());
    transfer.setSourceAccount(sourceAccount);
    transfer.SetTargetAccount(targetAccount);
    transfer.setAmountCents(convertToCents(request.getAmount()));
    transfer.setCurrency(request.getCurrency());
    transfer.setStatus(STATUS_PENDING);
    transfer.setClientRequestId(request.getClientRequestId());
    transfer.setReason(request.getReason());
    transfer.setCreatedAt(OffsetDateTime.now());
    transfer.setUpdatedAt(OffsetDateTime.now());

    Transfer savedTransfer = transferRepository.save(transfer);

    eventPublisherService.publishTransferCreated(
        savedTransfer.getTransferId(),
        savedTransfer.getSourceAccount().getAccountId(),
        savedTransfer.getTargetAccount().getAccountId(),
        request.getAmount(),
        request.getCurrency(),
        request.getClientRequestId(),
        request.getReason());

    try {
      processTransfer(savedTransfer);
    } catch (Exception e) {
      savedTransfer.setStatus(STATUS_FAILED);
      savedTransfer
          .setFailureCode(e instanceof TransferException ? ((TransferException) e).getFailureCode() : "UNKNOWN_ERROR");
      savedTransfer.setReason(e.getMessage());
      savedTransfer.setUpdatedAt(OffsetDateTime.now());
      transferRepository.save(savedTransfer);

      eventPublisherService.publishTransferProcessed(
          savedTransfer.getTransferId(),
          savedTransfer.getSourceAccount().getAccountId(),
          savedTransfer.getTargetAccount().getAccountId(),
          request.getAmount(),
          request.getCurrency(),
          STATUS_FAILED,
          request.getClientRequestId(),
          e.getMessage());
    }
    return convertToResponse(savedTransfer);
  }

  public TransferResponse createTransferFallback(CreateTransferRequest request, Exception ex) {
    TransferResponse response = new TransferResponse();
    response.setTransferId(UUID.randomUUID());
    response.setSourceAccountId(request.getSourceAccountId());
    response.setTargetAccountId(request.getTargetAccountId());
    response.setAmount(request.getAmount());
    response.setCurrency(request.getCurrency());
    response.setStatus(STATUS_FAILED);
    response.setClientRequestId(request.getClientRequestId());
    response.setReason("Service temporarily unavailable due to circuit breaker");
    response.setFailureCode("CIRCUIT_BREAKER_OPEN");
    response.setCreatedAt(OffsetDateTime.now());
    response.setUpdatedAt(OffsetDateTime.now());
    return response;
  }

  public void processTransfer(Transfer transfer) {
    if (!STATUS_PENDING.equals(transfer.getStatus())) {
      throw new TransferException("There is not in pending status", "INVALID_STATUS");
    }

    Account sourceAccount = transfer.getSourceAccount();
    Account targetAccount = transfer.getTargetAccount();

    // Check if source account has sufficent funds
    if (sourceAccount.getCurrentBalanceCents() < transfer.getAmountCents()) {
      throw new TransferException("Insufficent funds", FAILURE_INSUFFICENT_FUNDS);
    }

    // Perform the transfer atomically
    sourceAccount.setCurrentBalanceCents(sourceAccount.getCurrentBalanceCents() - transfer.getAmountCents());
    targetAccount.setCurrentBalanceCents(targetAccount.getCurrentBalanceCents() + transfer.getAmountCents());

    // Update accounts
    accountRepository.save(sourceAccount);
    accountRepository.save(targetAccount);

    // ledgerEntryService.createLedgerEntries(transfer, sourceAccount,
    // targetAccount);

    BigDecimal changeAmount = convertFromCents(transfer.getAmountCents());
    BigDecimal previousbalance = convertFromCents(sourceAccount.getCurrentBalanceCents() + transfer.getAmountCents());
    BigDecimal targetPreviousBalance = convertFromCents(
        targetAccount.getCurrentBalanceCents() - transfer.getAmountCents());

    eventPublisherService.publishAccountBalanceUpdate(
        sourceAccount.getAccountId(),
        previousbalance,
        sourceAccount.getCurrentBalance(),
        changeAmount.negate(),
        transfer.getCurrency(),
        transfer.getTransferId());

    eventPublisherService.publishAccountBalanceUpdate(
        targetAccount.getAccountId(),
        targetPreviousBalance,
        targetAccount.getCurrentBalance(),
        changeAmount,
        transfer.getCurrency(),
        transfer.getTransferId());

    // Update Transfer status
    transfer.setStatus(STATUS_COMPLETED);
    transfer.setUpdatedAt(OffsetDateTime.now());
    transferRepository.save(transfer);

    eventPublisherService.publishTransferProcessed(
        transfer.getTransferId(),
        transfer.getSourceAccount().getAccountId(),
        transfer.getTargetAccount().getAccountId(),
        convertFromCents(transfer.getAmountCents()),
        transfer.getCurrency(),
        STATUS_COMPLETED,
        transfer.getClientRequestId(),
        transfer.getReason());
  }

  public TransferResponse getTransferById(UUID transferId) {
    Transfer transfer = transferRepository.findById(transferId)
        .orElseThrow(() -> new TransferException("Transfer Not Found", "TRANSFER_NOT_FOUND"));
    return convertToResponse(transfer);
  }

  public List<TransferResponse> getTransfersByAccount(UUID accountId) {
    List<Transfer> transfers = transferRepository.findByAccountId(accountId);
    return convertToResponse(transfers);

  }

  @Transactional
  public TransferResponse cancelTransferById(UUID transferId) {
    Transfer transfer = transferRepository.findById(transferId)
        .orElseThrow(() -> new TransferException("Transfer not found", "TRANSFER_NOT_FOUND"));
    transfer.setStatus(STATUS_CANCELLED);
    transferRepository.save(transfer);
    return convertToResponse(transfer);
  }

  @Transactional
  public void processPendingTransfers() {
    List<Transfer> transfers = transferRepository.findByStatus(STATUS_PENDING);
    transfers.forEach(transfer -> {
      try {
        processTransfer(transfer);
      } catch (Exception e) {
        transfer.setStatus(STATUS_FAILED);
        transfer
            .setFailureCode(e instanceof TransferException ? ((TransferException) e).getFailureCode() : "UNKOWN_ERROR");
        transfer.setReason(e.getMessage());
        transfer.setUpdatedAt(OffsetDateTime.now());
        transferRepository.save(transfer);
      }
    });
  }

  private void validateTransferRequest(CreateTransferRequest request) {
    if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new TransferException("Invalid amount", FAILURE_INVALID_AMOUNT);
    }
    if (request.getSourceAccountId() == null || request.getTargetAccountId() == null) {
      throw new TransferException("Account IDs are required", FAILURE_ACCOUNT_NOT_FOUND);
    }
    if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
      throw new TransferException("Source and target accounts cannot be the same", FAILURE_SAME_ACCOUNT);
    }
  }

  private void validateAccountsAndTransfer(Account sourceAccount, Account targetAccount,
      CreateTransferRequest request) {
    if (!sourceAccount.getCurrency().equals(request.getCurrency()) ||
        !targetAccount.getCurrency().equals(request.getCurrency())) {
      throw new TransferException("Currency mismatch", FAILURE_CURRENCY_MISMATCH);
    }

    if (sourceAccount.getCurrentBalanceCents() < convertToCents(request.getAmount())) {
      throw new TransferException("Insufficent funds", FAILURE_INSUFFICENT_FUNDS);
    }
  }

  private long convertToCents(BigDecimal amount) {
    return amount.multiply(BigDecimal.valueOf(100)).longValue();
  }

  private BigDecimal convertFromCents(long cents) {
    return BigDecimal.valueOf(cents).divide(BigDecimal.valueOf(100));
  }

  private TransferResponse convertToResponse(Transfer transfer) {
    TransferResponse response = new TransferResponse();
    response.setTransferId(transfer.getTransferId());
    response.setSourceAccountId(transfer.getSourceAccount().getAccountId());
    response.setTargetAccountId(transfer.getTargetAccount().getAccountId());
    response.setAmount(convertFromCents(transfer.getAmountCents()));
    response.setCurrency(transfer.getCurrency());
    response.setStatus(transfer.getStatus());
    response.setClientRequestId(transfer.getClientRequestId());
    response.setReason(transfer.getReason());
    response.setFailureCode(transfer.getFailureCode());
    response.setCreatedAt(transfer.getCreatedAt());
    response.setUpdatedAt(transfer.getUpdatedAt());
    return response;
  }

  private List<TransferResponse> convertToResponse(List<Transfer> transfers) {
    return transfers.stream().map(this::convertToResponse).collect(Collectors.toList());
  }

  public static class TransferException extends RuntimeException {
    private final String failureCode;

    public TransferException(String message, String failureCode) {
      super(message);
      this.failureCode = failureCode;
    }

    public String getFailureCode() {
      return failureCode;
    }
  }
}
