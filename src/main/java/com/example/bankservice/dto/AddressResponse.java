package com.example.bankservice.dto;

public record AddressResponse(
        Long id,
        String street,
        String city,
        String state,
        String pinCode
) {
}
