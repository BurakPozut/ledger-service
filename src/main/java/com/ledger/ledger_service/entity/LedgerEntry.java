package com.ledger.ledger_service.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ledger_entry", schema = "ledger")
public class LedgerEntry {

  @Id
  @Column(name = "ledger_entry_id")
  private UUID ledgerEntryID;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @ManyToOne
  @JoinColumn(name = "transfer_id")
  private Transfer transfer;

  @Column(name = "direction", nullable = false)
  private String direction; // DEBIT or CREADIT

  @Column(name = "amount_cents", nullable = false)
  private Long amountCents;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  @Column(name = "occurred_at", nullable = false)
  private OffsetDateTime occurredAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  public LedgerEntry() {

  }

  public LedgerEntry(UUID ledgerEntryId, Account account, Transfer transfer, String direction, Long amountCents,
      String currency) {
    this.ledgerEntryID = ledgerEntryId;
    this.account = account;
    this.transfer = transfer;
    this.direction = direction;
    this.amountCents = amountCents;
    this.currency = currency;
  }

  public UUID getLedgerEntryId() {
    return ledgerEntryID;
  }

  public void setLedgerEntryId(UUID ledgerEntryId) {
    this.ledgerEntryID = ledgerEntryId;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Transfer getTransfer() {
    return transfer;
  }

  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
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

  public OffsetDateTime getOccurredAt() {
    return occurredAt;
  }

  public void setOccurredAt(OffsetDateTime occurredAt) {
    this.occurredAt = occurredAt;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
