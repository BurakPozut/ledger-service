package com.ledger.ledger_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ledger.ledger_service.entity.Transfer;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {
    
    // Find transfers by source account
    List<Transfer> findBySourceAccountAccountId(UUID sourceAccountId);
    
    // Find transfers by target account
    List<Transfer> findByTargetAccountAccountId(UUID targetAccountId);
    
    // Find transfers by status
    List<Transfer> findByStatus(String status);
    
    // Idempotency check: find by source account and client request ID
    Optional<Transfer> findBySourceAccountAccountIdAndClientRequestId(UUID sourceAccountId, String clientRequestId);
    
    // Find transfers for an account (both as source and target)
    @Query("SELECT t FROM Transfer t WHERE t.sourceAccount.accountId = :accountId OR t.targetAccount.accountId = :accountId")
    List<Transfer> findByAccountId(@Param("accountId") UUID accountId);
    
    // Find pending transfers (for processing)
    List<Transfer> findByStatusOrderByCreatedAtAsc(String status);
}