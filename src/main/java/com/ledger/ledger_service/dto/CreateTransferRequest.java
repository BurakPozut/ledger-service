package com.ledger.ledger_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateTransferRequest {

  @NotNull(message = "Source account ID is required")
  private UUID sourceAccountId;
  @NotNull(message = "Target account ID is required")
  private UUID targetAccountId;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greated than 0")
  private BigDecimal amount;

  @NotBlank(message = "Currency is required")
  @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter uppercase code")
  private String currency;

  @NotBlank(message = "Client request ID is required")
  private String clientRequestId;

  private String reason;

  public CreateTransferRequest() {
  }

  public CreateTransferRequest(UUID sourceAccountId, UUID targetAccountId, BigDecimal amount, String currency,
      String clientRequestId) {
    this.sourceAccountId = sourceAccountId;
    this.targetAccountId = targetAccountId;
    this.amount = amount;
    this.currency = currency;
    this.clientRequestId = clientRequestId;
  }

  public UUID getSourceAccountId() {
    return sourceAccountId;
  }

  public void setSourceAccountId(UUID sourceAccountId) {
    this.sourceAccountId = sourceAccountId;
  }

  public UUID getTargetAccountId() {
    return targetAccountId;
  }

  public void setTargetAccountId(UUID targetAccountId) {
    this.targetAccountId = targetAccountId;
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

  public String getClientRequestId() {
    return clientRequestId;
  }

  public void setClientRequestId(String clientRequestId) {
    this.clientRequestId = clientRequestId;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

}
