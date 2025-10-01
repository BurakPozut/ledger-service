package com.ledger.ledger_service.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ledger.ledger_service.dto.Reponse.AccountResponse;
import com.ledger.ledger_service.dto.Request.CreateAccountRequest;
import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.repository.AccountRepository;

@Service
public class AccountService {

  @Autowired
  private AccountRepository accountRepository;

  public List<Account> getAllAccounts() {
    return accountRepository.findAll();
  }

  public Optional<Account> getAccountById(UUID accountId) {
    return accountRepository.findById(accountId);
  }

  public List<Account> getAccountsByCurreny(String currency) {
    return accountRepository.findByCurrency(currency);
  }

  public List<Account> getAccountsByName(String name) {
    return accountRepository.findByNameContainingIgnoreCase(name);
  }

  public AccountResponse createAccount(CreateAccountRequest request) {
    validateAccountRequest(request);

    Account account = new Account();
    account.setAccountId(UUID.randomUUID());
    account.setName(request.getName());
    account.setCurrency(request.getCurrency());
    account.setCurrentBalance(request.getCurrentBalanceCents());
    account.setCreatAt(OffsetDateTime.now());
    account.setUpdateAt(OffsetDateTime.now());

    Account savedAccount = accountRepository.save(account);
    AccountResponse response = new AccountResponse();
    response.setAccountId(savedAccount.getAccountId());
    response.setName(savedAccount.getName());
    response.setCurrency(savedAccount.getCurrency());
    response.setCurrentBalanceCents(savedAccount.getCurrentBalance());
    response.setCreatedAt(savedAccount.getCreatedAt());
    response.setUpdatedAt(savedAccount.getUpdateAt());
    return response;
  }

  private void validateAccountRequest(CreateAccountRequest request) {
    if (request.getName().isBlank()) {
      throw new RuntimeException("Name is required");
    }
    if (request.getCurrency().isBlank()) {
      throw new RuntimeException("Currency is required");
    }
    if (request.getCurrentBalanceCents().compareTo(BigDecimal.ZERO) < 0) {
      throw new RuntimeException("Current balance can not be smaller than 0");
    }
  }
}
