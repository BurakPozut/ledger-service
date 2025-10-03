package com.ledger.ledger_service.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AccountBalanceUpdatedEvent {
  private final UUID accountId;
  private final BigDecimal previousBalance;
  private final BigDecimal newBalance;
  private final BigDecimal changeAmount;
  private final String currency;
  private final UUID transferId;
  private final OffsetDateTime occurredAt;

  public AccountBalanceUpdatedEvent(UUID accountId, BigDecimal previousBalance,
      BigDecimal newBalance, BigDecimal changeAmount,
      String currency, UUID transferId, OffsetDateTime occurredAt) {
    this.accountId = accountId;
    this.previousBalance = previousBalance;
    this.newBalance = newBalance;
    this.changeAmount = changeAmount;
    this.currency = currency;
    this.transferId = transferId;
    this.occurredAt = occurredAt;
  }

  // Getters
  public UUID getAccountId() {
    return accountId;
  }

  public BigDecimal getPreviousBalance() {
    return previousBalance;
  }

  public BigDecimal getNewBalance() {
    return newBalance;
  }

  public BigDecimal getChangeAmount() {
    return changeAmount;
  }

  public String getCurrency() {
    return currency;
  }

  public UUID getTransferId() {
    return transferId;
  }

  public OffsetDateTime getOccurredAt() {
    return occurredAt;
  }
}
