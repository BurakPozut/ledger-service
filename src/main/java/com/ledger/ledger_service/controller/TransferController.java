package com.ledger.ledger_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledger.ledger_service.dto.Reponse.TransferResponse;
import com.ledger.ledger_service.dto.Request.CreateTransferRequest;
import com.ledger.ledger_service.service.TransferService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

  @Autowired
  private TransferService transferService;

  @PostMapping
  public ResponseEntity<TransferResponse> createTransfer(@Valid @RequestBody CreateTransferRequest request) {
    TransferResponse response = transferService.executeTransfer(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{transferId}")
  public ResponseEntity<TransferResponse> getTransferById(@PathVariable UUID transferId) {
    TransferResponse response = transferService.getTransferById(transferId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/account/{accountId}")
  public ResponseEntity<List<TransferResponse>> getTransfersForAccount(@PathVariable UUID accountId) {
    List<TransferResponse> transfers = transferService.getTransfersByAccount(accountId);
    return ResponseEntity.ok(transfers);
  }

  @PutMapping("{transferId}/cancel")
  public ResponseEntity<TransferResponse> cancelTransfer(@PathVariable UUID transferId) {
    TransferResponse response = transferService.cancelTransferById(transferId);
    return ResponseEntity.ok(response);
  }

  // Admin endpoint
  @PostMapping("/process-pending")
  public ResponseEntity<String> processPendingTransfers() {
    transferService.processPendingTransfers();
    return ResponseEntity.ok("Pending transfers processed");
  }
}
