package com.example.bankservice.mapper;

import com.example.bankservice.domain.Account;
import com.example.bankservice.domain.Address;
import com.example.bankservice.domain.Bank;
import com.example.bankservice.dto.AccountResponse;
import com.example.bankservice.dto.AddressRequest;
import com.example.bankservice.dto.AddressResponse;
import com.example.bankservice.dto.BankRequest;
import com.example.bankservice.dto.BankResponse;

import java.util.Locale;

public final class BankMapper {

    private BankMapper() {
    }

    public static Bank toBank(BankRequest request) {
        return new Bank(request.bankName().trim(), normalizeIfsc(request.ifscCode()),
                request.branchName().trim(), toAddress(request.address()));
    }

    public static Address toAddress(AddressRequest request) {
        return new Address(request.street().trim(), request.city().trim(),
                request.state().trim(), request.pinCode());
    }

    public static String normalizeIfsc(String ifscCode) {
        return ifscCode.trim().toUpperCase(Locale.ROOT);
    }

    public static AddressResponse toResponse(Address address) {
        return new AddressResponse(address.getId(), address.getStreet(), address.getCity(),
                address.getState(), address.getPinCode());
    }

    public static BankResponse toResponse(Bank bank) {
        return new BankResponse(bank.getId(), bank.getBankName(), bank.getIfscCode(),
                bank.getBranchName(), toResponse(bank.getAddress()),
                bank.getCreatedAt(), bank.getUpdatedAt());
    }

    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(),
                account.getAccountHolderName(), account.getBalance(), account.getAccountType(),
                toResponse(account.getBank()), account.getCreatedAt(), account.getUpdatedAt());
    }
}
