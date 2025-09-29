package com.ledger.ledger_service.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.ledger_service.dto.CreateTransferRequest;
import com.ledger.ledger_service.dto.TransferResponse;
import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.entity.Transfer;
import com.ledger.ledger_service.repository.AccountRepository;
import com.ledger.ledger_service.repository.TransferRepository;

@Service
public class TransferService {

  @Autowired
  private TransferRepository transferRepository;

  @Autowired
  private AccountRepository accountRepository;

  private static final String STATUS_PRENDING = "PENDING";

  private static final String STATUS_COMPLETED = "COMPLETED";

  private static final String STATUS_FAILED = "FAILED";

  private static final String STATUS_CANCELLED = "CANCELLED";

  private static final String FAILURE_INSUFFICENT_FUNDS = "INSUFFICENT_FUNDS";
  private static final String FAILURE_ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";
  private static final String FAILURE_CURRENCY_MISMATCH = "CURRENCY_MISMATCH";
  private static final String FAILURE_SAME_ACCOUNT = "SAME_ACCOUNT";
  private static final String FAILURE_DUPLICATE_REQUEST = "DUPLICATE_REQUEST";
  private static final String FAILURE_INVALID_AMOUNT = "INVALID_AMOUNT";

  @Transactional
  public TransferResponse createTransfer(CreateTransferRequest request) {
    validateTransferRequest(request);

    // Check for duplicate requests (idempotency)
    Optional<Transfer> existingTransfer = transferRepository
        .findBySourceAccountAccountIdAndClientRequestId(request.getSourceAccountId(), request.getClientRequestId());

    if (existingTransfer.isPresent()) {
      return convertToResponse(existingTransfer.get());
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
    transfer.setStatus(STATUS_PRENDING);
    transfer.setClientRequestId(request.getClientRequestId());
    transfer.setReason(request.getReason());
    transfer.setCreatedAt(OffsetDateTime.now());
    transfer.setUpdatedAt(OffsetDateTime.now());

    Transfer savedTransfer = transferRepository.save(transfer);

    try {
      processTransfer(savedTransfer);
    } catch (Exception e) {
      savedTransfer.setStatus(STATUS_FAILED);
      savedTransfer
          .setFailureCode(e instanceof TransferException ? ((TransferException) e).getFailureCore() : "UNKNOWN_ERROR");
      savedTransfer.setReason(e.getMessage());
      savedTransfer.setUpdatedAt(OffsetDateTime.now());
      transferRepository.save(savedTransfer);
    }
    return convertToResponse(savedTransfer);
  }

  @Transactional
  public void processTransfer(Transfer transfer) {
    if (!STATUS_PRENDING.equals(transfer.getStatus())) {
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

    // Update Transfer status
    transfer.setStatus(STATUS_COMPLETED);
    transfer.setUpdatedAt(OffsetDateTime.now());
    transferRepository.save(transfer);
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

  // TODO: I not sure I did this correctly maybe add transactional at the top?
  public TransferResponse cancelTransferById(UUID transferId) {
    Transfer transfer = transferRepository.findById(transferId)
        .orElseThrow(() -> new TransferException("Transfer not found", "TRANSFER_NOT_FOUND"));
    transfer.setStatus(STATUS_CANCELLED);
    transferRepository.save(transfer);
    return convertToResponse(transfer);
  }

  public TransferResponse processPendingTransfers() {
    List<Transfer> transfers = transferRepository.findByStatus(STATUS_PRENDING);
    transfers.forEach(transfer -> {
      try {
        processTransfer(transfer);
      } catch (Exception e) {
        transfer.setStatus(STATUS_FAILED);
        transfer
            .setFailureCode(e instanceof TransferException ? ((TransferException) e).getFailureCore() : "UNKONW_ERROR");
        transfer.setReason(e.getMessage());
        transfer.setUpdatedAt(OffsetDateTime.now());
        transferRepository.save(transfer);
      }
    });
    return null;
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

    public String getFailureCore() {
      return failureCode;
    }
  }
}
