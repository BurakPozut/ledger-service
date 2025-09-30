package com.ledger.ledger_service.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ledger.ledger_service.entity.Account;
import com.ledger.ledger_service.repository.AccountRepository;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceOptimisticLockingTest {

  @Autowired
  private AccountRepository accountRepository;

  @Test
  public void testOptimisticLockingWithConcurrentUpdates() throws InterruptedException {
    Account account = new Account();
    account.setAccountId(UUID.randomUUID());
    account.setName("Test Account");
    account.setCurrency("USD");
    account.setCurrentBalanceCents(100000L);
    account.setCreatAt(OffsetDateTime.now());
    account.setUpdateAt(OffsetDateTime.now());

    Account savedAccount = accountRepository.saveAndFlush(account);
    Integer originalVersion = account.getVersion();

    // Simulate concurrent updates
    CountDownLatch latch = new CountDownLatch(2);
    AtomicReference<Exception> exception1 = new AtomicReference<>();
    AtomicReference<Exception> exception2 = new AtomicReference<>();

    Thread thread1 = new Thread(() -> {
      try {
        Thread.sleep(10);
        Account account1 = accountRepository.findById(savedAccount.getAccountId()).orElseThrow();
        account1.setCurrentBalanceCents(account1.getCurrentBalanceCents() + 1000L);
        accountRepository.save(account1);
      } catch (Exception e) {
        exception1.set(e);
      } finally {
        latch.countDown();
      }
    });

    // Thread 2: Update name (simultaneously)
    Thread thread2 = new Thread(() -> {
      try {
        Thread.sleep(10);
        Account account2 = accountRepository.findById(savedAccount.getAccountId()).orElseThrow();
        account2.setName("Updated name");
        accountRepository.save(account2);
      } catch (Exception e) {
        exception2.set(e);
      } finally {
        latch.countDown();
      }
    });

    thread1.start();
    thread2.start();

    // Wait for both to complete
    latch.await(5, TimeUnit.SECONDS);

    boolean oneSucceeded = exception1.get() == null || exception2.get() == null;
    boolean oneFailed = exception1.get() != null || exception2.get() != null;

    assertTrue(oneSucceeded, "At least on update should succeed");
    assertTrue(oneFailed, "At least one update should fail due to optimistic locking");

    boolean hasOptimisticLockingException = (exception1.get() instanceof ObjectOptimisticLockingFailureException) ||
        (exception2.get() instanceof ObjectOptimisticLockingFailureException);

    assertTrue(hasOptimisticLockingException, "One of the updates should fail with OptimisticLockingFailureException");

    Account updatedAccount = accountRepository.findById(savedAccount.getAccountId()).orElseThrow();
    assertTrue(updatedAccount.getVersion() > originalVersion,
        "Version should have been incremented from " + originalVersion + " to " + updatedAccount.getVersion());

    System.out.println("Original version: " + originalVersion);
    System.out.println("Final version: " + updatedAccount.getVersion());
    System.out.println("Thread 1 exception: " + exception1.get());
    System.out.println("Thread 2 exception: " + exception2.get());
  }

  @Test
  public void testOptimisticLockingWithTransferSimulation() throws InterruptedException {
    Account account1 = new Account();
    account1.setAccountId(UUID.randomUUID());
    account1.setName("Account 1");
    account1.setCurrency("USD");
    account1.setCurrentBalanceCents(100000L);
    account1.setCreatAt(OffsetDateTime.now());
    account1.setUpdateAt(OffsetDateTime.now());

    Account account2 = new Account();
    account2.setAccountId(UUID.randomUUID());
    account2.setName("Account 2");
    account2.setCurrency("USD");
    account2.setCurrentBalanceCents(50000L);
    account2.setCreatAt(OffsetDateTime.now());
    account2.setUpdateAt(OffsetDateTime.now());

    Account savedAccount1 = accountRepository.save(account1);
    Account savedAccount2 = accountRepository.save(account2);

    Integer originalVersion1 = savedAccount1.getVersion();
    Integer originalVersion2 = savedAccount2.getVersion();

    CountDownLatch latch = new CountDownLatch(2);
    AtomicReference<Exception> exception1 = new AtomicReference<>();
    AtomicReference<Exception> exception2 = new AtomicReference<>();

    Thread thread1 = new Thread(() -> {
      try {
        Account acc1 = accountRepository.findById(savedAccount1.getAccountId()).orElseThrow();
        acc1.setCurrentBalanceCents(acc1.getCurrentBalanceCents() - 10000L);
        accountRepository.save(acc1);
      } catch (Exception e) {
        exception1.set(e);
      } finally {
        latch.countDown();
      }
    });

    Thread thread2 = new Thread(() -> {
      try {
        Account acc2 = accountRepository.findById(savedAccount2.getAccountId()).orElseThrow();
        acc2.setCurrentBalanceCents(acc2.getCurrentBalanceCents() + 10000L);
        accountRepository.save(acc2);
      } catch (Exception e) {
        exception2.set(e);
      } finally {
        latch.countDown();
      }
    });

    thread1.start();
    thread2.start();
    latch.await(5, TimeUnit.SECONDS);

    assertNull(exception1.get(), "Thread 1 should not have failed: " + exception1.get());
    assertNull(exception2.get(), "Thread 2 should not have failed: " + exception2.get());

    // Verify versions were incremented
    Account updatedAccount1 = accountRepository.findById(account1.getAccountId()).orElseThrow();
    Account updatedAccount2 = accountRepository.findById(account2.getAccountId()).orElseThrow();

    assertTrue(updatedAccount1.getVersion() > originalVersion1, "Account 1 version should have been incremented");
    assertTrue(updatedAccount2.getVersion() > originalVersion2, "Account 2 version should have been incremented");
  }

}
