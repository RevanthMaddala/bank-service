package com.example.bankservice.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "bank.account-number")
public record AccountNumberProperties(@Min(9) @Max(18) int length) {
}
