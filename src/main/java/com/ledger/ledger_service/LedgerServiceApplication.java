package com.ledger.ledger_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LedgerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LedgerServiceApplication.class, args);
	}
}
// TODO: Seitch this application to use events instead of direct calls. Use
// ledger entry as an event not a service maybe?
// neceserry)
