package com.ledger.ledger_service.dto.Reponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class LedgerSummaryResponse {
  private String currency;
  private OffsetDateTime fromDate;
  private OffsetDateTime toDate;
  private int totalEntries;
  private BigDecimal totalDebits;
  private BigDecimal totalCredits;
  private int debitCount;
  private int creditCount;
  private BigDecimal netAmount;

  // Default constructor
  public LedgerSummaryResponse() {
  }

  // Constructor with all fields
  public LedgerSummaryResponse(String currency, OffsetDateTime fromDate, OffsetDateTime toDate,
      int totalEntries, BigDecimal totalDebits, BigDecimal totalCredits,
      int debitCount, int creditCount, BigDecimal netAmount) {
    this.currency = currency;
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.totalEntries = totalEntries;
    this.totalDebits = totalDebits;
    this.totalCredits = totalCredits;
    this.debitCount = debitCount;
    this.creditCount = creditCount;
    this.netAmount = netAmount;
  }

  // Getters
  public String getCurrency() {
    return currency;
  }

  public OffsetDateTime getFromDate() {
    return fromDate;
  }

  public OffsetDateTime getToDate() {
    return toDate;
  }

  public int getTotalEntries() {
    return totalEntries;
  }

  public BigDecimal getTotalDebits() {
    return totalDebits;
  }

  public BigDecimal getTotalCredits() {
    return totalCredits;
  }

  public int getDebitCount() {
    return debitCount;
  }

  public int getCreditCount() {
    return creditCount;
  }

  public BigDecimal getNetAmount() {
    return netAmount;
  }

  // Setters
  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public void setFromDate(OffsetDateTime fromDate) {
    this.fromDate = fromDate;
  }

  public void setToDate(OffsetDateTime toDate) {
    this.toDate = toDate;
  }

  public void setTotalEntries(int totalEntries) {
    this.totalEntries = totalEntries;
  }

  public void setTotalDebits(BigDecimal totalDebits) {
    this.totalDebits = totalDebits;
  }

  public void setTotalCredits(BigDecimal totalCredits) {
    this.totalCredits = totalCredits;
  }

  public void setDebitCount(int debitCount) {
    this.debitCount = debitCount;
  }

  public void setCreditCount(int creditCount) {
    this.creditCount = creditCount;
  }

  public void setNetAmount(BigDecimal netAmount) {
    this.netAmount = netAmount;
  }
}
