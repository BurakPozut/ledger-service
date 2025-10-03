package com.ledger.ledger_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.event.AccountBalanceUpdatedEvent;
import com.ledger.ledger_service.repository.AccountRepository;

@Service
public class AccountBalanceEventHandler {

  @Autowired
  private AccountRepository accountRepository;

  @EventListener
  @Transactional
  public void handleAccountBalanceUpdate(AccountBalanceUpdatedEvent event) {
    // This is where we can add additional logic like when balance changes call
    // notification service etc

    Account account = accountRepository.findById(event.getAccountId())
        .orElseThrow(() -> new RuntimeException("Account not found: " + event.getAccountId()));

    System.out.println("Account " + event.getAccountId() + " balance changed from  " +
        event.getPreviousBalance() + " to " + event.getNewBalance() + " (change: " + event.getChangeAmount() + ")");
  }
}
