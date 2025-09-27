package com.ledger.ledger_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ledger.ledger_service.repository.TransferRepository;

@Service
public class TransferService {

  @Autowired
  private TransferRepository transferRepository;
}
