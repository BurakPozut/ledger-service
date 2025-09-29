package com.ledger.ledger_service.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "transfer", schema = "ledger")
public class Transfer {

  @Id
  @Column(name = "transfer_id", nullable = false)
  private UUID transferId;

  @ManyToOne
  @JoinColumn(name = "source_account_id", nullable = false)
  private Account sourceAccount;

  @ManyToOne
  @JoinColumn(name = "target_account_id", nullable = false)
  private Account targetAccount;

  @Column(name = "amount_cents", nullable = false)
  private Long amountCents;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "client_request_id", nullable = false)
  private String clientRequestId;

  @Column(name = "reason")
  private String reason;

  @Column(name = "failure_code")
  private String failureCode;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  public Transfer() {
  }

  public Transfer(UUID transferId, Account sourceAccount, Account targeAccount, Long amountCents, String currency,
      String status, String clientRequestId) {
    this.transferId = transferId;
    this.sourceAccount = sourceAccount;
    this.targetAccount = targeAccount;
    this.amountCents = amountCents;
    this.currency = currency;
    this.status = status;
    this.clientRequestId = clientRequestId;
  }

  public UUID getTransferId() {
    return transferId;
  }

  public void setTransferId(UUID transferId) {
    this.transferId = transferId;
  }

  public Account getSourceAccount() {
    return sourceAccount;
  }

  public void setSourceAccount(Account sourceAccount) {
    this.sourceAccount = sourceAccount;
  }

  public Account getTargetAccount() {
    return targetAccount;
  }

  public void SetTargetAccount(Account targetAccount) {
    this.targetAccount = targetAccount;
  }

  public Long getAmountCents() {
    return amountCents;
  }

  public void setAmountCents(Long amountCents) {
    this.amountCents = amountCents;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public String getFailureCode() {
    return failureCode;
  }

  public void setFailureCode(String failureCode) {
    this.failureCode = failureCode;
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

  @Override
  public String toString() {
    return "Transfer{" +
        "transferId=" + transferId +
        ", sourceAccount=" + sourceAccount +
        ", targetAccount=" + targetAccount +
        ", amountCents=" + amountCents +
        ", currency='" + currency + '\'' +
        ", status='" + status + '\'' +
        ", clientRequestId='" + clientRequestId + '\'' +
        ", reason='" + reason + '\'' +
        ", failureCode='" + failureCode + '\'' +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        '}';
  }
}
