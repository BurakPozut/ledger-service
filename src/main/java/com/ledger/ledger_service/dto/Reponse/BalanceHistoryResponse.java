package com.ledger.ledger_service.dto.Reponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class BalanceHistoryResponse {
  private OffsetDateTime date;
  private BigDecimal balance;
  private BigDecimal debitTotal;
  private BigDecimal creditTotal;
  private int entryCount;

  // Default constructor
  public BalanceHistoryResponse() {
  }

  // Constructor with all fields
  public BalanceHistoryResponse(OffsetDateTime date, BigDecimal balance, BigDecimal debitTotal,
      BigDecimal creditTotal, int entryCount) {
    this.date = date;
    this.balance = balance;
    this.debitTotal = debitTotal;
    this.creditTotal = creditTotal;
    this.entryCount = entryCount;
  }

  // Getters
  public OffsetDateTime getDate() {
    return date;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public BigDecimal getDebitTotal() {
    return debitTotal;
  }

  public BigDecimal getCreditTotal() {
    return creditTotal;
  }

  public int getEntryCount() {
    return entryCount;
  }

  // Setters
  public void setDate(OffsetDateTime date) {
    this.date = date;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public void setDebitTotal(BigDecimal debitTotal) {
    this.debitTotal = debitTotal;
  }

  public void setCreditTotal(BigDecimal creditTotal) {
    this.creditTotal = creditTotal;
  }

  public void setEntryCount(int entryCount) {
    this.entryCount = entryCount;
  }
}
