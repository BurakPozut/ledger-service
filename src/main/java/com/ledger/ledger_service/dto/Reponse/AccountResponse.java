package com.ledger.ledger_service.dto.Reponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AccountResponse {
  private UUID accountId;
  private String name;
  private String currency;
  private BigDecimal currentBalanceCents;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public AccountResponse() {
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getCurrentBalanceCents() {
    return currentBalanceCents;
  }

  public void setCurrentBalanceCents(BigDecimal currentBalanceCents) {
    this.currentBalanceCents = currentBalanceCents;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

}
