package com.example.bankservice.dto;

import com.example.bankservice.domain.AccountType;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id,
        String accountNumber,
        String accountHolderName,
        BigDecimal balance,
        AccountType accountType,
        BankResponse bank,
        Instant createdAt,
        Instant updatedAt
) {
}
