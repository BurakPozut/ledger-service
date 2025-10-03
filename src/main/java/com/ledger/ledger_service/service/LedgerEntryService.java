package com.ledger.ledger_service.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.ledger_service.dto.Reponse.BalanceHistoryResponse;
import com.ledger.ledger_service.dto.Reponse.LedgerEntryResponse;
import com.ledger.ledger_service.dto.Reponse.LedgerSummaryResponse;
import com.ledger.ledger_service.dto.Reponse.ReconciliationResponse;
import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.entity.LedgerEntry;
// import com.ledger.ledger_service.entity.Transfer;
import com.ledger.ledger_service.repository.AccountRepository;
import com.ledger.ledger_service.repository.LedgerEntryRepository;

@Service
public class LedgerEntryService {

  @Autowired
  private LedgerEntryRepository ledgerEntryRepository;

  @Autowired
  private AccountRepository accountRepository;

  public List<LedgerEntryResponse> getLedgerEntriesByAccount(UUID accountId,
      OffsetDateTime fromDate, OffsetDateTime toDate, String direction) {
    List<LedgerEntry> entries;

    if (fromDate != null && toDate != null) {
      if (direction != null) {
        entries = ledgerEntryRepository.findByAccountAccountIdAndDirectionAndOccurredAtBetween(accountId, direction,
            fromDate, toDate);
      } else {
        entries = ledgerEntryRepository.findAccountEntriesInDateRange(accountId, fromDate, toDate);
      }
    } else if (direction != null) {
      entries = ledgerEntryRepository.findByAccountAccountIdAndDirection(accountId, direction);
    } else {
      entries = ledgerEntryRepository.findByAccountAccountId(accountId);
    }
    return entries.stream().map(this::convertToResponse).collect(Collectors.toList());
  }

  // reconcile account balance
  public ReconciliationResponse reconcileAccount(UUID accountId) {
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new RuntimeException("Account not found"));

    Long ledgerBalanceCents = ledgerEntryRepository.getAccountBalanceAsOf(accountId, OffsetDateTime.now());

    BigDecimal materializedBalance = account.getCurrentBalance();
    BigDecimal ledgerBalance = BigDecimal.valueOf(ledgerBalanceCents).divide(BigDecimal.valueOf(100));

    boolean isReconciled = materializedBalance.equals(ledgerBalance);

    ReconciliationResponse response = new ReconciliationResponse();
    response.setAccountId(accountId);
    response.setMaterializedBalance(materializedBalance);
    response.setLedgerBalance(ledgerBalance);
    response.setReconciled(isReconciled);

    if (!isReconciled) {
      response.setDiscrepancy(ledgerBalance.subtract(materializedBalance).toString());
    }

    return response;
  }

  // Get ledger entries for transfer
  public List<LedgerEntryResponse> getLedgerEntriesByTransfer(UUID transferId) {
    System.out.println("Looking for transfer ID: " + transferId);
    List<LedgerEntry> entries = ledgerEntryRepository.findByTransferTransferId(transferId);
    System.out.println("Found " + entries.size() + " entries");
    return entries.stream().map(this::convertToResponse).collect(Collectors.toList());
  }

  // Get account balance history (reconstructed from ledger)
  public List<BalanceHistoryResponse> getAccountBalanceHistory(UUID accountId, OffsetDateTime fromDate,
      OffsetDateTime toDate) {
    if (fromDate == null) {
      fromDate = OffsetDateTime.now().minus(30, ChronoUnit.DAYS);
    }
    if (toDate == null) {
      toDate = OffsetDateTime.now();
    }

    List<LedgerEntry> entries = ledgerEntryRepository.findAccountEntriesInDateRange(accountId, fromDate, toDate);

    return calculateBalanceHistory(entries, fromDate, toDate);
  }

  public LedgerSummaryResponse getLedgerSummary(String currency, OffsetDateTime fromDate, OffsetDateTime toDate) {
    if (fromDate == null) {
      fromDate = OffsetDateTime.now().minus(30, ChronoUnit.DAYS);
    }
    if (toDate == null) {
      toDate = OffsetDateTime.now();
    }

    List<LedgerEntry> entries;
    if (currency != null) {
      entries = ledgerEntryRepository.findByCurrencyAndOccurredAtBetween(currency, fromDate, toDate);
    } else {
      entries = ledgerEntryRepository.findByOccurredAtBetween(fromDate, toDate);
    }

    return calculateLedgerSummary(entries, currency, fromDate, toDate);
  }

  // @Transactional
  // public void createLedgerEntries(Transfer transfer, Account sourceAccount,
  // Account targerAccount) {
  // OffsetDateTime now = OffsetDateTime.now();

  // // Debit Entry for source account
  // LedgerEntry debitEntry = new LedgerEntry();
  // debitEntry.setLedgerEntryId(UUID.randomUUID());
  // debitEntry.setAccount(sourceAccount);
  // debitEntry.setTransfer(transfer);
  // debitEntry.setDirection("DEBIT");
  // debitEntry.setAmountCents(transfer.getAmountCents());
  // debitEntry.setCurrency(transfer.getCurrency());
  // debitEntry.setOccurredAt(now);
  // debitEntry.setCreatedAt(now);

  // // Credit entry for targer account
  // LedgerEntry creditEntry = new LedgerEntry();
  // creditEntry.setLedgerEntryId(UUID.randomUUID());
  // creditEntry.setAccount(targerAccount);
  // creditEntry.setTransfer(transfer);
  // creditEntry.setDirection("CREDIT");
  // creditEntry.setAmountCents(transfer.getAmountCents());
  // creditEntry.setCurrency(transfer.getCurrency());
  // creditEntry.setOccurredAt(now);
  // creditEntry.setCreatedAt(now);

  // ledgerEntryRepository.save(debitEntry);
  // ledgerEntryRepository.save(creditEntry);
  // }

  private LedgerEntryResponse convertToResponse(LedgerEntry entry) {
    LedgerEntryResponse response = new LedgerEntryResponse();
    response.setLedgerEntryId(entry.getLedgerEntryId());
    response.setAccountId(entry.getAccount().getAccountId());
    response.setAccountName(entry.getAccount().getName());
    response.setTransferId(entry.getTransfer() != null ? entry.getTransfer().getTransferId() : null);
    response.setDirection(entry.getDirection());
    response.setAmount(BigDecimal.valueOf(entry.getAmountCents()).divide(BigDecimal.valueOf(100)));
    response.setCurrency(entry.getCurrency());
    response.setOccurredAt(entry.getOccurredAt());
    response.setCreatedAt(entry.getCreatedAt());
    return response;
  }

  private List<BalanceHistoryResponse> calculateBalanceHistory(List<LedgerEntry> entries, OffsetDateTime fromDate,
      OffsetDateTime toDate) {
    List<BalanceHistoryResponse> history = new ArrayList<>();
    Map<String, BalanceHistoryResponse> dailyBalances = new HashMap<>();

    for (LedgerEntry entry : entries) {
      String dateKey = entry.getOccurredAt().toLocalDate().toString();

      BalanceHistoryResponse dayBalance = dailyBalances.computeIfAbsent(dateKey, k -> {
        BalanceHistoryResponse response = new BalanceHistoryResponse();
        response
            .setDate(entry.getOccurredAt().toLocalDate().atStartOfDay().atOffset(entry.getOccurredAt().getOffset()));
        response.setBalance(BigDecimal.ZERO);
        response.setDebitTotal(BigDecimal.ZERO);
        response.setCreditTotal(BigDecimal.ZERO);
        response.setEntryCount(0);
        return response;
      });

      BigDecimal amount = BigDecimal.valueOf(entry.getAmountCents()).divide(BigDecimal.valueOf(100));

      if ("DEBIT".equals(entry.getDirection())) {
        dayBalance.setDebitTotal(dayBalance.getDebitTotal().add(amount));
        dayBalance.setBalance(dayBalance.getBalance().subtract(amount));
      } else {
        dayBalance.setCreditTotal(dayBalance.getCreditTotal().add(amount));
        dayBalance.setBalance(dayBalance.getBalance().add(amount));
      }
      dayBalance.setEntryCount(dayBalance.getEntryCount() + 1);
    }

    history.addAll(dailyBalances.values());
    history.sort((a, b) -> a.getDate().compareTo(b.getDate()));

    return history;
  }

  private LedgerSummaryResponse calculateLedgerSummary(List<LedgerEntry> entries, String currency,
      OffsetDateTime fromDate, OffsetDateTime toDate) {
    LedgerSummaryResponse summary = new LedgerSummaryResponse();
    summary.setCurrency(currency);
    summary.setFromDate(fromDate);
    summary.setToDate(toDate);
    summary.setTotalEntries(entries.size());

    BigDecimal totalDebits = BigDecimal.ZERO;
    BigDecimal totalCredits = BigDecimal.ZERO;
    int debitCount = 0;
    int creditCount = 0;

    for (LedgerEntry entry : entries) {
      BigDecimal amount = BigDecimal.valueOf(entry.getAmountCents()).divide(BigDecimal.valueOf(100));

      if ("DEBIT".equals(entry.getDirection())) {
        totalDebits = totalDebits.add(amount);
        debitCount++;
      } else {
        totalCredits = totalCredits.add(amount);
        creditCount++;
      }

    }
    summary.setTotalDebits(totalDebits);
    summary.setTotalCredits(totalCredits);
    summary.setDebitCount(debitCount);
    summary.setCreditCount(creditCount);
    summary.setNetAmount(totalCredits.subtract(totalDebits));

    return summary;
  }
}
