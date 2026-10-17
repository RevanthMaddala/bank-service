package com.example.bankservice.service;

import com.example.bankservice.config.AccountNumberProperties;
import com.example.bankservice.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {

    private static final int MAX_ATTEMPTS = 10;

    private final SecureRandom random = new SecureRandom();
    private final AccountRepository accountRepository;
    private final int length;

    public AccountNumberGenerator(AccountRepository accountRepository, AccountNumberProperties properties) {
        this.accountRepository = accountRepository;
        this.length = properties.length();
    }

    /**
     * Generates a numeric account number not currently in use. The unique constraint on
     * the column remains the final guard against a concurrent insert of the same number.
     */
    public String generate() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String candidate = randomDigits();
            if (!accountRepository.existsByAccountNumber(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Unable to generate a unique account number");
    }

    private String randomDigits() {
        StringBuilder sb = new StringBuilder(length);
        sb.append(1 + random.nextInt(9));
        for (int i = 1; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
