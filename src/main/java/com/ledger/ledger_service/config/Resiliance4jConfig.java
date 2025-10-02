package com.ledger.ledger_service.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;

@Configuration
public class Resiliance4jConfig {
  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    return CircuitBreakerRegistry.ofDefaults();
  }

  @Bean
  public RetryRegistry retryRegistry() {
    return RetryRegistry.ofDefaults();
  }

  @Bean
  public TimeLimiterRegistry timeLimiterRegistry() {
    return TimeLimiterRegistry.ofDefaults();
  }

  @Bean
  public CircuitBreaker transferServiceCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofSeconds(30))
        .slidingWindowSize(10)
        .minimumNumberOfCalls(5)
        .permittedNumberOfCallsInHalfOpenState(3)
        .slowCallRateThreshold(50)
        .slowCallDurationThreshold(Duration.ofSeconds(2))
        .build();

    return circuitBreakerRegistry.circuitBreaker("transferService", config);
  }

  @Bean
  public CircuitBreaker accountServiceCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        .failureRateThreshold(50)
        .waitDurationInOpenState(Duration.ofSeconds(30))
        .slidingWindowSize(10)
        .minimumNumberOfCalls(5)
        .permittedNumberOfCallsInHalfOpenState(3)
        .slowCallRateThreshold(50)
        .slowCallDurationThreshold(Duration.ofSeconds(2))
        .build();

    return circuitBreakerRegistry.circuitBreaker("accountService", config);
  }

  @Bean
  public Retry transferServiceRetry(RetryRegistry retryRegistry) {
    RetryConfig config = RetryConfig.custom()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .retryExceptions(Exception.class)
        .build();

    return retryRegistry.retry("transferService", config);
  }

  @Bean
  public Retry accountServiceRetry(RetryRegistry retryRegistry) {
    RetryConfig config = RetryConfig.custom()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .retryExceptions(Exception.class)
        .build();

    return retryRegistry.retry("accountService", config);
  }

  @Bean
  public TimeLimiter transferServiceTimeLimiter(TimeLimiterRegistry timeLimiterRegistry) {
    TimeLimiterConfig config = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(5))
        .build();

    return timeLimiterRegistry.timeLimiter("transferService", config);
  }

  @Bean
  public TimeLimiter accountServiceTimeLimiter(TimeLimiterRegistry timeLimiterRegistry) {
    TimeLimiterConfig config = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(5))
        .build();

    return timeLimiterRegistry.timeLimiter("accountService", config);
  }
}
