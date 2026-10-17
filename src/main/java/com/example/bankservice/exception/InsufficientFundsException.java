package com.example.bankservice.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountNumber, BigDecimal balance, BigDecimal requested) {
        super("Insufficient funds in account %s: balance %s, requested %s"
                .formatted(accountNumber, balance.toPlainString(), requested.toPlainString()));
    }
}
