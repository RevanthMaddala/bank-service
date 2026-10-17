package com.example.bankservice.repository;

import com.example.bankservice.domain.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {

    @EntityGraph(attributePaths = "address")
    Optional<Bank> findWithAddressById(Long id);

    @EntityGraph(attributePaths = "address")
    Page<Bank> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = "address")
    Optional<Bank> findByIfscCode(String ifscCode);

    boolean existsByIfscCode(String ifscCode);

    boolean existsByIfscCodeAndIdNot(String ifscCode, Long id);
}
