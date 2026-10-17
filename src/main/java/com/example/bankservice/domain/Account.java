package com.example.bankservice.domain;

import com.example.bankservice.exception.InsufficientFundsException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "account")
public class Account extends BaseEntity {

    @Column(name = "account_number", nullable = false, unique = true, updatable = false, length = 20)
    private String accountNumber;

    @Column(name = "account_holder_name", nullable = false, length = 150)
    private String accountHolderName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    protected Account() {
    }

    public Account(String accountNumber, String accountHolderName, BigDecimal openingBalance,
                   AccountType accountType, Bank bank) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = normalize(openingBalance);
        this.accountType = accountType;
        this.bank = bank;
    }

    public void update(String accountHolderName, AccountType accountType) {
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(normalize(amount));
    }

    public void withdraw(BigDecimal amount) {
        BigDecimal normalized = normalize(amount);
        if (balance.compareTo(normalized) < 0) {
            throw new InsufficientFundsException(accountNumber, balance, normalized);
        }
        this.balance = this.balance.subtract(normalized);
    }

    private static BigDecimal normalize(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Bank getBank() {
        return bank;
    }
}
