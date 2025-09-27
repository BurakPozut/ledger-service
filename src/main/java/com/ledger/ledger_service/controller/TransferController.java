package com.ledger.ledger_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ledger.ledger_service.service.TransferService;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

  @Autowired
  private TransferService transferService;

  // TODO: POST, GET {id}, GET /account/{transferId}, GET /status/{transferId},
  // GET /status/{status}
}
