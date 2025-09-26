package com.ledger.ledger_service.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.service.AccountService;


@RestController
@RequestMapping("/api/accounts")
public class AccountController {
  
  @Autowired
  private AccountService accountService;

  @GetMapping
  public ResponseEntity<List<Account>> getAllAccounts() {
    List<Account> accounts = accountService.getAllAccounts();
    return ResponseEntity.ok(accounts);
  }

  @GetMapping("/{accountID}")
  public ResponseEntity<Account> getAccountById(@PathVariable UUID accountId){
    Optional<Account> account = accountService.getAccountById(accountId);
    return account.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
  
  @GetMapping("/currency/{currency}")
  public ResponseEntity<List<Account>> getAccountsByCurrency(@PathVariable String currency) {
    List<Account> accounts = accountService.getAccountsByCurreny(currency.toUpperCase());
    return ResponseEntity.ok(accounts);
  }

  @GetMapping("/search")
  public ResponseEntity<List<Account>> searchAccountsByName(@RequestParam String name) {
    List<Account> accounts = accountService.getAccountsByName(name);
    return ResponseEntity.ok(accounts);
  }
  
  
}
