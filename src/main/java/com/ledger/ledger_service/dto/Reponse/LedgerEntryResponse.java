package com.ledger.ledger_service.dto.Reponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class LedgerEntryResponse {
  private UUID ledgerEntryId;
  private UUID accountId;
  private String accountName;
  private UUID transferId;
  private String direction;
  private BigDecimal amount;
  private String currency;
  private OffsetDateTime occurredAt;
  private OffsetDateTime createdAt;

  public LedgerEntryResponse() {

  }

  public LedgerEntryResponse(UUID ledgerEntryId, UUID accountId, String accountName, UUID transferId,
      String direction, BigDecimal amount, String currency, OffsetDateTime occurredAt, OffsetDateTime createdAt) {
    this.ledgerEntryId = ledgerEntryId;
    this.accountId = accountId;
    this.accountName = accountName;
    this.transferId = transferId;
    this.direction = direction;
    this.amount = amount;
    this.currency = currency;
    this.occurredAt = occurredAt;
    this.createdAt = createdAt;
  }

  public UUID getLedgerEntryId() {
    return ledgerEntryId;
  }

  public void setLedgerEntryId(UUID ledgerEntryId) {
    this.ledgerEntryId = ledgerEntryId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public UUID getTransferId() {
    return transferId;
  }

  public void setTransferId(UUID transferId) {
    this.transferId = transferId;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public OffsetDateTime getOccurredAt() {
    return occurredAt;
  }

  public void setOccurredAt(OffsetDateTime occuredAt) {
    this.occurredAt = occuredAt;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

}
