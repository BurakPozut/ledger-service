package com.ledger.ledger_service.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TransferEvent {
  private final UUID transferId;
  private final UUID sourceAccountId;
  private final UUID targetAccountId;
  private final BigDecimal amount;
  private final String currency;
  private final String status;
  private final String clientRequestId;
  private final String reason;
  private final OffsetDateTime occurredAt;

  public TransferEvent(UUID transferId, UUID sourceAccountId, UUID targetAccountId, BigDecimal amount, String currency,
      String status, String clientRequestId, String reason, OffsetDateTime occurredAt) {

    this.transferId = transferId;
    this.sourceAccountId = sourceAccountId;
    this.targetAccountId = targetAccountId;
    this.amount = amount;
    this.currency = currency;
    this.status = status;
    this.clientRequestId = clientRequestId;
    this.reason = reason;
    this.occurredAt = occurredAt;
  }

  public UUID getTransferId() {
    return transferId;
  }

  public UUID getSourceAccountId() {
    return sourceAccountId;
  }

  public UUID getTargetAccountId() {
    return targetAccountId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public String getStatus() {
    return status;
  }

  public String getClientRequestId() {
    return clientRequestId;
  }

  public String getReason() {
    return reason;
  }

  public OffsetDateTime getOccurredAt() {
    return occurredAt;
  }
}
