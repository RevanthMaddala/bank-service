package com.example.bankservice.service;

import com.example.bankservice.dto.BankRequest;
import com.example.bankservice.dto.BankResponse;
import com.example.bankservice.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BankService {

    BankResponse create(BankRequest request);

    BankResponse getById(Long id);

    BankResponse getByIfsc(String ifscCode);

    PageResponse<BankResponse> list(Pageable pageable);

    BankResponse update(Long id, BankRequest request);

    void delete(Long id);
}
