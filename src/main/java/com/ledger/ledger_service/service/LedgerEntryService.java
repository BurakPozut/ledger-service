package com.ledger.ledger_service.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.ledger_service.dto.LedgerEntryResponse;
import com.ledger.ledger_service.dto.ReconciliationResponse;
import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.entity.LedgerEntry;
import com.ledger.ledger_service.entity.Transfer;
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

  // Get ledger summary
  // public LedgerSummaryResponse getLedgerSummary(String currency, OffsetDateTime
  // fromDate, OffsetDateTime tOffsetDateTime){

  // }
  @Transactional
  public void createLedgerEntries(Transfer transfer, Account sourceAccount, Account targerAccount) {
    OffsetDateTime now = OffsetDateTime.now();

    // Debit Entry for source account
    LedgerEntry debitEntry = new LedgerEntry();
    debitEntry.setLedgerEntryId(UUID.randomUUID());
    debitEntry.setAccount(sourceAccount);
    debitEntry.setTransfer(transfer);
    debitEntry.setDirection("DEBIT");
    debitEntry.setAmountCents(transfer.getAmountCents());
    debitEntry.setCurrency(transfer.getCurrency());
    debitEntry.setOccurredAt(now);
    debitEntry.setCreatedAt(now);

    // Credit entry for targer account
    LedgerEntry creditEntry = new LedgerEntry();
    creditEntry.setLedgerEntryId(UUID.randomUUID());
    creditEntry.setAccount(targerAccount);
    creditEntry.setTransfer(transfer);
    creditEntry.setDirection("CREDIT");
    creditEntry.setAmountCents(transfer.getAmountCents());
    creditEntry.setCurrency(transfer.getCurrency());
    creditEntry.setOccurredAt(now);
    creditEntry.setCreatedAt(now);

    ledgerEntryRepository.save(debitEntry);
    ledgerEntryRepository.save(creditEntry);
  }

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

  // Caluclate Balance history

}
