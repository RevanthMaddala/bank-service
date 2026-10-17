package com.example.bankservice.domain;

import com.example.bankservice.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    private Account newAccount(String openingBalance) {
        Bank bank = new Bank("State Bank", "SBIN0001234", "MG Road",
                new Address("1 MG Road", "Bengaluru", "Karnataka", "560001"));
        return new Account("123456789012", "Asha Rao", new BigDecimal(openingBalance),
                AccountType.SAVINGS, bank);
    }

    @Test
    void depositIncreasesBalance() {
        Account account = newAccount("100.00");
        account.deposit(new BigDecimal("50.25"));
        assertThat(account.getBalance()).isEqualByComparingTo("150.25");
    }

    @Test
    void withdrawDecreasesBalance() {
        Account account = newAccount("100.00");
        account.withdraw(new BigDecimal("100"));
        assertThat(account.getBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void withdrawMoreThanBalanceIsRejected() {
        Account account = newAccount("10.00");
        assertThatThrownBy(() -> account.withdraw(new BigDecimal("10.01")))
                .isInstanceOf(InsufficientFundsException.class);
        assertThat(account.getBalance()).isEqualByComparingTo("10.00");
    }

    @Test
    void balanceIsStoredWithTwoDecimals() {
        assertThat(newAccount("5").getBalance()).hasToString("5.00");
    }
}
