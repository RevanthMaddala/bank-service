package com.example.bankservice.service.impl;

import com.example.bankservice.domain.Account;
import com.example.bankservice.domain.Bank;
import com.example.bankservice.dto.AccountCreateRequest;
import com.example.bankservice.dto.AccountResponse;
import com.example.bankservice.dto.AccountUpdateRequest;
import com.example.bankservice.dto.PageResponse;
import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.mapper.BankMapper;
import com.example.bankservice.repository.AccountRepository;
import com.example.bankservice.repository.BankRepository;
import com.example.bankservice.service.AccountNumberGenerator;
import com.example.bankservice.service.AccountService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository,
                              AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @Override
    @Transactional
    public AccountResponse create(AccountCreateRequest request) {
        Bank bank = bankRepository.findWithAddressById(request.bankId())
                .orElseThrow(() -> new ResourceNotFoundException("Bank", "id", request.bankId()));
        Account account = new Account(accountNumberGenerator.generate(),
                request.accountHolderName().trim(), request.openingBalance(),
                request.accountType(), bank);
        return BankMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse getByAccountNumber(String accountNumber) {
        return BankMapper.toResponse(findAccount(accountNumber));
    }

    @Override
    public PageResponse<AccountResponse> list(Long bankId, Pageable pageable) {
        var page = bankId == null
                ? accountRepository.findAllBy(pageable)
                : accountRepository.findByBankId(bankId, pageable);
        return PageResponse.from(page.map(BankMapper::toResponse));
    }

    @Override
    @Transactional
    public AccountResponse update(String accountNumber, AccountUpdateRequest request) {
        Account account = findAccount(accountNumber);
        account.update(request.accountHolderName().trim(), request.accountType());
        accountRepository.flush();
        return BankMapper.toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse deposit(String accountNumber, BigDecimal amount) {
        Account account = findAccountForUpdate(accountNumber);
        account.deposit(amount);
        accountRepository.flush();
        return BankMapper.toResponse(account);
    }

    @Override
    @Transactional
    public AccountResponse withdraw(String accountNumber, BigDecimal amount) {
        Account account = findAccountForUpdate(accountNumber);
        account.withdraw(amount);
        accountRepository.flush();
        return BankMapper.toResponse(account);
    }

    @Override
    @Transactional
    public void delete(String accountNumber) {
        accountRepository.delete(findAccount(accountNumber));
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "account number", accountNumber));
    }

    private Account findAccountForUpdate(String accountNumber) {
        return accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "account number", accountNumber));
    }
}
