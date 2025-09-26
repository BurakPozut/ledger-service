package com.ledger.ledger_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ledger.ledger_service.entity.TransferStatusLut;

@Repository
public interface TransferStatusLutRepository extends JpaRepository<TransferStatusLut, String> {
    
    // This is a simple lookup table, so basic CRUD operations are sufficient
    // The status codes are: PENDING, APPROVED, REJECTED, COMPLETED, FAILED
}