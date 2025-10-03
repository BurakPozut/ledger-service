package com.ledger.ledger_service.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.entity.LedgerEntry;
import com.ledger.ledger_service.entity.Transfer;
import com.ledger.ledger_service.event.TransferEvent;
import com.ledger.ledger_service.repository.AccountRepository;
import com.ledger.ledger_service.repository.LedgerEntryRepository;
import com.ledger.ledger_service.repository.TransferRepository;

@Service
public class LedgerEntryEventHandler {

  @Autowired
  private LedgerEntryRepository ledgerEntryRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TransferRepository transferRepository;

  @EventListener
  @Transactional
  public void handleTransferProcessed(TransferEvent event) {
    if (!"COMPLETED".equals(event.getStatus())) {
      return;
    }

    Transfer transfer = transferRepository.findById(event.getTransferId())
        .orElseThrow(() -> new RuntimeException("Transfer not found: " + event.getTransferId()));
    Account sourceAccount = accountRepository.findById(event.getSourceAccountId())
        .orElseThrow(() -> new RuntimeException("Source Account not found: " + event.getSourceAccountId()));
    Account targetAccount = accountRepository.findById(event.getTargetAccountId())
        .orElseThrow(() -> new RuntimeException("Target Account not found: " + event.getTargetAccountId()));

    createLedgerEntries(transfer, sourceAccount, targetAccount);

  }

  private void createLedgerEntries(Transfer transfer, Account sourceAccount, Account targetAccount) {
    OffsetDateTime now = OffsetDateTime.now();
    long amountCents = transfer.getAmountCents();

    LedgerEntry debitEntry = new LedgerEntry();
    debitEntry.setLedgerEntryId(UUID.randomUUID());
    debitEntry.setAccount(sourceAccount);
    debitEntry.setTransfer(transfer);
    debitEntry.setDirection("DEBIT");
    debitEntry.setAmountCents(amountCents);
    debitEntry.setCurrency(transfer.getCurrency());
    debitEntry.setOccurredAt(now);
    debitEntry.setCreatedAt(now);

    // Credit entry for target account
    LedgerEntry creditEntry = new LedgerEntry();
    creditEntry.setLedgerEntryId(UUID.randomUUID());
    creditEntry.setAccount(targetAccount);
    creditEntry.setTransfer(transfer);
    creditEntry.setDirection("CREDIT");
    creditEntry.setAmountCents(amountCents);
    creditEntry.setCurrency(transfer.getCurrency());
    creditEntry.setOccurredAt(now);
    creditEntry.setCreatedAt(now);

    ledgerEntryRepository.save(debitEntry);
    ledgerEntryRepository.save(creditEntry);
  }

}
