package com.example.bankservice.dto;

import com.example.bankservice.domain.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Balance and bank are intentionally not updatable here: balance changes only via
 * deposit/withdraw, and moving an account between banks is not a supported operation.
 */
public record AccountUpdateRequest(
        @NotBlank @Size(max = 150) String accountHolderName,
        @NotNull AccountType accountType
) {
}
