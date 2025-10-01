package com.ledger.ledger_service.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledger.ledger_service.dto.BalanceHistoryResponse;
import com.ledger.ledger_service.dto.LedgerEntryResponse;
import com.ledger.ledger_service.dto.LedgerSummaryResponse;
import com.ledger.ledger_service.dto.ReconciliationResponse;
import com.ledger.ledger_service.service.LedgerEntryService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/ledger")
public class LedgerEntryController {

  @Autowired
  private LedgerEntryService ledgerEntryService;

  @GetMapping("/account/{accountId}")
  public ResponseEntity<List<LedgerEntryResponse>> getLedgerEntriesbyAccount(
      @PathVariable UUID accountId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate,
      @RequestParam(required = false) String direction) {
    List<LedgerEntryResponse> entries = ledgerEntryService.getLedgerEntriesByAccount(accountId, fromDate, toDate,
        direction);
    return ResponseEntity.ok(entries);
  }

  @GetMapping("/transfer/{transferId}")
  public ResponseEntity<List<LedgerEntryResponse>> getLedgerEntriesByTransfer(@PathVariable UUID transferId) {
    List<LedgerEntryResponse> entries = ledgerEntryService.getLedgerEntriesByTransfer(transferId);
    return ResponseEntity.ok(entries);
  }

  @GetMapping("/account/{accountId}/balance-history")
  public ResponseEntity<List<BalanceHistoryResponse>> getAccountBalanceHistory(
      @PathVariable UUID accountId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
    List<BalanceHistoryResponse> history = ledgerEntryService.getAccountBalanceHistory(accountId, fromDate, toDate);
    return ResponseEntity.ok(history);
  }

  @GetMapping("/account/{accountId}/reconcile")
  public ResponseEntity<ReconciliationResponse> reconcileAccount(@PathVariable UUID accountId) {
    ReconciliationResponse reconciliation = ledgerEntryService.reconcileAccount(accountId);
    return ResponseEntity.ok(reconciliation);
  }

  @GetMapping("/summary")
  public ResponseEntity<LedgerSummaryResponse> getLedgerSummary(@RequestParam(required = false) String currency,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
    LedgerSummaryResponse summary = ledgerEntryService.getLedgerSummary(currency, fromDate, toDate);
    return ResponseEntity.ok(summary);
  }

}
