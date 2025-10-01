package com.ledger.ledger_service.dto.Reponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ReconciliationResponse {
  private UUID accountId;
  private BigDecimal materializedBalance;
  private BigDecimal ledgerBalance;
  private boolean isReconciled;
  private String discrepancy;
  private List<LedgerEntryResponse> unreconciledEntries;

  // Default constructor
  public ReconciliationResponse() {
  }

  // Constructor with all fields
  public ReconciliationResponse(UUID accountId, BigDecimal materializedBalance, BigDecimal ledgerBalance,
      boolean isReconciled, String discrepancy, List<LedgerEntryResponse> unreconciledEntries) {
    this.accountId = accountId;
    this.materializedBalance = materializedBalance;
    this.ledgerBalance = ledgerBalance;
    this.isReconciled = isReconciled;
    this.discrepancy = discrepancy;
    this.unreconciledEntries = unreconciledEntries;
  }

  // Getters
  public UUID getAccountId() {
    return accountId;
  }

  public BigDecimal getMaterializedBalance() {
    return materializedBalance;
  }

  public BigDecimal getLedgerBalance() {
    return ledgerBalance;
  }

  public boolean isReconciled() {
    return isReconciled;
  }

  public String getDiscrepancy() {
    return discrepancy;
  }

  public List<LedgerEntryResponse> getUnreconciledEntries() {
    return unreconciledEntries;
  }

  // Setters
  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public void setMaterializedBalance(BigDecimal materializedBalance) {
    this.materializedBalance = materializedBalance;
  }

  public void setLedgerBalance(BigDecimal ledgerBalance) {
    this.ledgerBalance = ledgerBalance;
  }

  public void setReconciled(boolean reconciled) {
    isReconciled = reconciled;
  }

  public void setDiscrepancy(String discrepancy) {
    this.discrepancy = discrepancy;
  }

  public void setUnreconciledEntries(List<LedgerEntryResponse> unreconciledEntries) {
    this.unreconciledEntries = unreconciledEntries;
  }
}
