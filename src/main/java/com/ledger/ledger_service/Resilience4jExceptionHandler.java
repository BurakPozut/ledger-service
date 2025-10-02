package com.ledger.ledger_service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.retry.MaxRetriesExceededException;

@RestControllerAdvice
public class Resilience4jExceptionHandler {

  @ExceptionHandler(CallNotPermittedException.class)
  public ResponseEntity<Map<String, Object>> handleCircuitBreakerOpen(CallNotPermittedException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Service temporarily unavailable");
    response.put("message", "Circuit breaker is open. Please try again later.");
    response.put("timestamp", OffsetDateTime.now());
    response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());

    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
  }

  @ExceptionHandler(MaxRetriesExceededException.class)
  public ResponseEntity<Map<String, Object>> handleMaxRetiresExceeded(MaxRetriesExceededException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Service retry limit exceeded");
    response.put("message", "Circuit breaker is open. Please try again later");
    response.put("timestamp", OffsetDateTime.now());
    response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());

    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
  }

  @ExceptionHandler(TimeoutException.class)
  public ResponseEntity<Map<String, Object>> handleTimeout(TimeoutException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Service timeout");
    response.put("message", "Service request timed out. Please try again later.");
    response.put("timestamp", OffsetDateTime.now());
    response.put("status", HttpStatus.REQUEST_TIMEOUT.value());

    return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response);
  }

}
