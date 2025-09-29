package com.ledger.ledger_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ledger.ledger_service.service.TransferService.TransferException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TransferException.class)
  public ResponseEntity<ErrorResponse> handleTransferException(TransferException e) {
    ErrorResponse error = new ErrorResponse(e.getMessage(), e.getFailureCode());
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
    ErrorResponse error = new ErrorResponse("Internal server error", "INTERNAL_ERROR");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  public static class ErrorResponse {
    private String message;
    private String failureCode;

    public ErrorResponse(String message, String failureCode) {
      this.message = message;
      this.failureCode = failureCode;
    }

    public String getMessage() {
      return message;
    }

    public String getFailureCode() {
      return failureCode;
    }
  }
}
