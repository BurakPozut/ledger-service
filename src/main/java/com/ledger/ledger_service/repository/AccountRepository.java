package com.ledger.ledger_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ledger.ledger_service.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

  List<Account> findByCurrency(String currency);

  List<Account> findByNameContainingIgnoreCase(String name);
}
