package com.example.bankservice.dto;

import com.example.bankservice.domain.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountCreateRequest(
        @NotBlank @Size(max = 150) String accountHolderName,
        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 17, fraction = 2) BigDecimal openingBalance,
        @NotNull AccountType accountType,
        @NotNull Long bankId
) {
}
