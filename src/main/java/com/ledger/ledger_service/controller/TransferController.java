package com.ledger.ledger_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledger.ledger_service.dto.CreateTransferRequest;
import com.ledger.ledger_service.dto.TransferResponse;
import com.ledger.ledger_service.service.TransferService;
import com.ledger.ledger_service.service.TransferService.TransferException;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

  @Autowired
  private TransferService transferService;

  // TODO: POST, GET {id}, GET /account/{transferId}, GET /status/{transferId},
  // GET /status/{status}

  @PostMapping
  public ResponseEntity<TransferResponse> createTransfer(@Valid @RequestBody CreateTransferRequest request) {
    try {
      TransferResponse response = transferService.createTransfer(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (TransferException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{transferId}")
  public ResponseEntity<TransferResponse> getTransferById(@PathVariable UUID transferId) {
    try {
      TransferResponse response = transferService.getTransferById(transferId);
      return ResponseEntity.ok(response);
    } catch (TransferException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/account/{accountId}")
  public ResponseEntity<List<TransferResponse>> getTransfersForAccount(@PathVariable UUID accountId) {
    List<TransferResponse> transfers = transferService.getTransfersByAccount(accountId);
    return ResponseEntity.ok(transfers);
  }

  @PutMapping("{transferId}/cancel")
  public ResponseEntity<TransferResponse> cancelTransfer(@PathVariable UUID transferId) {
    try {
      TransferResponse response = transferService.cancelTransferById(transferId);
      return ResponseEntity.ok(response);
    } catch (TransferException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // Admin endpoint
  @PostMapping("/process-pending")
  public ResponseEntity<String> processPendingTransfers() {
    transferService.processPendingTransfers();
    return ResponseEntity.ok("Pending transfers processed");
  }

}
