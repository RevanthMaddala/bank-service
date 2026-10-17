package com.example.bankservice.dto;

import java.time.Instant;

public record BankResponse(
        Long id,
        String bankName,
        String ifscCode,
        String branchName,
        AddressResponse address,
        Instant createdAt,
        Instant updatedAt
) {
}
