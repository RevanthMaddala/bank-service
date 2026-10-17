package com.example.bankservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank @Size(max = 255) String street,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String state,
        @NotBlank
        @Pattern(regexp = "^[1-9][0-9]{5}$", message = "must be a valid 6-digit PIN code")
        String pinCode
) {
}
