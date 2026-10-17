package com.example.bankservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BankRequest(
        @NotBlank @Size(max = 150) String bankName,
        @NotBlank
        @Pattern(regexp = "^[A-Za-z]{4}0[A-Za-z0-9]{6}$",
                message = "must be a valid 11-character IFSC code (e.g. SBIN0001234)")
        String ifscCode,
        @NotBlank @Size(max = 150) String branchName,
        @NotNull @Valid AddressRequest address
) {
}
