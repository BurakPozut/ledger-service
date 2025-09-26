package com.ledger.ledger_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class TestController {
  
  @GetMapping("/hello")
  public String hello() {
      return "Hello from ledger Service!";
  }

  @GetMapping("/health")
  public String health() {
      return "Service is running";
  }
  @GetMapping("/info")
  public String info() {
      return "Ledger Service v1.0 - Ready for development";
  }
  
  
  
}
