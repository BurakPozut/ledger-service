package com.ledger.ledger_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class CreateAccountRequest {

  @NotNull(message = "Name can not be empty")
  private String name;

  @NotNull(message = "Currency can not be empty")
  private String currency;

  @NotNull(message = "Current balance cents cannot be empty")
  private BigDecimal currentBalanceCents;

  public CreateAccountRequest() {
  }

  public CreateAccountRequest(String name, String currency, BigDecimal currentBalanceCents) {
    this.name = name;
    this.currency = currency;
    this.currentBalanceCents = currentBalanceCents;
  }

  public String getName() {
    return name;
  }

  // public void setName(String name) {
  // this.name = name;
  // }

  public String getCurrency() {
    return currency;
  }

  // public void setCurrency(String currency) {
  // this.currency = currency;
  // }

  public BigDecimal getCurrentBalanceCents() {
    return currentBalanceCents;
  }

  // public void setCurrentBalanceCents(BigDecimal currentBalanceCents) {
  // this.current_balance_cents = currentBalanceCents;
  // }
}
