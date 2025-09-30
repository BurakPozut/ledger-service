package com.ledger.ledger_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transfer_status_lut", schema = "ledger")
public class TransferStatusLut {

  @Id
  @Column(name = "code")
  private String code;

  public TransferStatusLut() {
  }

  public TransferStatusLut(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return "TransferStatusLut{" +
        "code='" + code + '\'' +
        '}';
  }
}
