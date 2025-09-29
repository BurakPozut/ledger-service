package com.ledger.ledger_service.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "account", schema = "ledger")
public class Account {

  @Id
  @Column(name = "account_id")
  private UUID accountId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "current_balance_cents", nullable = false)
  private Long currentBalanceCents;

  @Column(name = "version", nullable = false)
  private Integer version;

  @Version
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  public Account() {
  }

  public Account(UUID accountId, String name, String currency, long currentBalanceCents) {
    this.accountId = accountId;
    this.name = name;
    this.currency = currency;
    this.currentBalanceCents = currentBalanceCents;
    this.version = 0;
  }

  public BigDecimal getCurrentBalance() {
    return BigDecimal.valueOf(currentBalanceCents).divide(BigDecimal.valueOf(100));
  }

  public void setCurrentBalance(BigDecimal balance) {
    this.currentBalanceCents = balance.multiply(BigDecimal.valueOf(100)).longValue();
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public UUID getAccountId() {
    return accountId;
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

  public Long getCurrentBalanceCents() {
    return currentBalanceCents;
  }

  public void setCurrentBalanceCents(Long currentBalanceCents) {
    this.currentBalanceCents = currentBalanceCents;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdateAt() {
    return updatedAt;
  }

  public void setUpdateAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "Account{" +
        "accountId=" + accountId +
        ", name=" + name +
        ", currency=" + currency +
        ", currentBalanceCents=" + currentBalanceCents +
        ", version=" + version +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        '}';
  }
}
