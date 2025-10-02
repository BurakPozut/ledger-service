package com.ledger.ledger_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.github.resilience4j.micrometer.tagged.TaggedRetryMetrics;
import io.github.resilience4j.micrometer.tagged.TaggedTimeLimiterMetrics;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MonitoringConfig {

  @Bean
  public TaggedCircuitBreakerMetrics taggedCircuitBreakerMetrics(MeterRegistry meterRegistry) {
    return TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(
        CircuitBreakerRegistry.ofDefaults());
  }

  @Bean
  public TaggedRetryMetrics taggedRetryMetrics(MeterRegistry meterRegistry) {
    return TaggedRetryMetrics.ofRetryRegistry(RetryRegistry.ofDefaults());
  }

  @Bean
  public TaggedTimeLimiterMetrics taggedTimeLimiterMetrics(MeterRegistry meterRegistry) {
    return TaggedTimeLimiterMetrics.ofTimeLimiterRegistry(TimeLimiterRegistry.ofDefaults());
  }
}
