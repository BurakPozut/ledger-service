package com.ledger.ledger_service.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ledger.ledger_service.entity.LedgerEntry;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
  List<LedgerEntry> findByAccountAccountId(UUID accountId);

  List<LedgerEntry> findByTransferTransferId(UUID transferId);

  List<LedgerEntry> findByAccountAccountIdAndOccurredAtBetween(
      UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate);

  List<LedgerEntry> findByAccountAccountIdAndDirection(UUID accountId, String direction);

  List<LedgerEntry> findByAccountAccountIdAndDirectionAndOccurredAtBetween(
      UUID accountId, String direction, OffsetDateTime fromDate, OffsetDateTime toDate);

  @Query("SELECT le FROM LedgerEntry le WHERE le.account.accountId = :accountId " +
      "AND le.occurredAt BETWEEN :fromDate AND :toDate " +
      "ORDER BY le.occurredAt ASC")
  List<LedgerEntry> findAccountEntriesInDateRange(
      @Param("accountId") UUID accountId,
      @Param("fromDate") OffsetDateTime fromDate,
      @Param("toDate") OffsetDateTime toDate);

  // Get Balance at specific point in time
  @Query("SELECT COALESCE(SUM(CASE WHEN le.direction = 'CREDIT' THEN le.amountCents ELSE -le.amountCents END), 0) " +
      "FROM LedgerEntry le WHERE le.account.accountId = :accountId " +
      "AND le.occurredAt <= :asOfDate")
  Long getAccountBalanceAsOf(@Param("accountId") UUID accountId, @Param("asOfDate") OffsetDateTime asOfDate);

  List<LedgerEntry> findByCurrency(String currency);

  List<LedgerEntry> findByCurrencyAndOccurredAtBetween(String currency, OffsetDateTime fromDate, OffsetDateTime toDate);

  @Query("SELECT le FROM LedgerEntry le WHERE le.occurredAt BETWEEN :fromDate AND :toDate ORDER BY le.occurredAt ASC")
  List<LedgerEntry> findByOccurredAtBetween(
      @Param("fromDate") OffsetDateTime fromDate,
      @Param("toDate") OffsetDateTime toDate);
}
