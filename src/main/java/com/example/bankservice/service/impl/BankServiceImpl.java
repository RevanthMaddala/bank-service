package com.example.bankservice.service.impl;

import com.example.bankservice.domain.Bank;
import com.example.bankservice.dto.AddressRequest;
import com.example.bankservice.dto.BankRequest;
import com.example.bankservice.dto.BankResponse;
import com.example.bankservice.dto.PageResponse;
import com.example.bankservice.exception.ConflictException;
import com.example.bankservice.exception.ResourceNotFoundException;
import com.example.bankservice.mapper.BankMapper;
import com.example.bankservice.repository.AccountRepository;
import com.example.bankservice.repository.BankRepository;
import com.example.bankservice.service.BankService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final AccountRepository accountRepository;

    public BankServiceImpl(BankRepository bankRepository, AccountRepository accountRepository) {
        this.bankRepository = bankRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public BankResponse create(BankRequest request) {
        String ifsc = BankMapper.normalizeIfsc(request.ifscCode());
        if (bankRepository.existsByIfscCode(ifsc)) {
            throw new ConflictException("Bank with IFSC code '%s' already exists".formatted(ifsc));
        }
        Bank saved = bankRepository.save(BankMapper.toBank(request));
        return BankMapper.toResponse(saved);
    }

    @Override
    public BankResponse getById(Long id) {
        return BankMapper.toResponse(findBank(id));
    }

    @Override
    public BankResponse getByIfsc(String ifscCode) {
        String ifsc = BankMapper.normalizeIfsc(ifscCode);
        return bankRepository.findByIfscCode(ifsc)
                .map(BankMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", "IFSC code", ifsc));
    }

    @Override
    public PageResponse<BankResponse> list(Pageable pageable) {
        return PageResponse.from(bankRepository.findAllBy(pageable).map(BankMapper::toResponse));
    }

    @Override
    @Transactional
    public BankResponse update(Long id, BankRequest request) {
        Bank bank = findBank(id);
        String ifsc = BankMapper.normalizeIfsc(request.ifscCode());
        if (bankRepository.existsByIfscCodeAndIdNot(ifsc, id)) {
            throw new ConflictException("Bank with IFSC code '%s' already exists".formatted(ifsc));
        }
        bank.update(request.bankName().trim(), ifsc, request.branchName().trim());
        AddressRequest address = request.address();
        bank.getAddress().update(address.street().trim(), address.city().trim(),
                address.state().trim(), address.pinCode());
        bankRepository.flush();
        return BankMapper.toResponse(bank);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Bank bank = findBank(id);
        if (accountRepository.existsByBankId(id)) {
            throw new ConflictException("Bank %d cannot be deleted because it has accounts".formatted(id));
        }
        bankRepository.delete(bank);
    }

    private Bank findBank(Long id) {
        return bankRepository.findWithAddressById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", "id", id));
    }
}
