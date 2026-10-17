package com.example.bankservice.service;

import com.example.bankservice.dto.AccountCreateRequest;
import com.example.bankservice.dto.AccountResponse;
import com.example.bankservice.dto.AccountUpdateRequest;
import com.example.bankservice.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface AccountService {

    AccountResponse create(AccountCreateRequest request);

    AccountResponse getByAccountNumber(String accountNumber);

    PageResponse<AccountResponse> list(Long bankId, Pageable pageable);

    AccountResponse update(String accountNumber, AccountUpdateRequest request);

    AccountResponse deposit(String accountNumber, BigDecimal amount);

    AccountResponse withdraw(String accountNumber, BigDecimal amount);

    void delete(String accountNumber);
}
