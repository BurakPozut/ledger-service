package com.ledger.ledger_service.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.ledger.ledger_service.event.AccountBalanceUpdatedEvent;
import com.ledger.ledger_service.event.TransferEvent;

@Service
public class EventPublisherService {
  @Autowired
  private ApplicationEventPublisher eventPublisher;

  public void publishTransferCreated(UUID transferId, UUID sourceAccountId, UUID targetAccountID, BigDecimal amount,
      String currnecy, String clientRequestId, String reason) {
    TransferEvent event = new TransferEvent(transferId, sourceAccountId, targetAccountID, amount, currnecy, "PENDING",
        clientRequestId, reason, OffsetDateTime.now());
    eventPublisher.publishEvent(event);
  }

  public void publishTransferProcessed(UUID transferId, UUID sourceAcccountId, UUID targetAccountId, BigDecimal amount,
      String currency, String status, String clientRequestId, String reason) {
    TransferEvent event = new TransferEvent(transferId, sourceAcccountId, targetAccountId, amount, currency, status,
        clientRequestId, reason, OffsetDateTime.now());
    eventPublisher.publishEvent(event);
  }

  public void publishAccountBalanceUpdate(UUID accountId, BigDecimal previousBalance, BigDecimal newBalance,
      BigDecimal changeAmount, String currency, UUID transferId) {
    AccountBalanceUpdatedEvent event = new AccountBalanceUpdatedEvent(accountId, previousBalance, newBalance,
        changeAmount, currency, transferId, OffsetDateTime.now());
    eventPublisher.publishEvent(event);
  }

}
