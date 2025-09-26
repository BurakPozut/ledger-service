package com.ledger.ledger_service.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.repository.AccountRepository;

@Service
public class AccountService {
  
  @Autowired
  private AccountRepository accountRepository;

  public List<Account> getAllAccounts(){
    return accountRepository.findAll();
  }

  public Optional<Account> getAccountById(UUID accountId){
    return accountRepository.findById(accountId);
  }

  public List<Account> getAccountsByCurreny(String currency){
    return accountRepository.findByCurrency(currency);
  }

  public List<Account> getAccountsByName(String name){
    return accountRepository.findByNameContainingIgnoreCase(name);
  }
}
