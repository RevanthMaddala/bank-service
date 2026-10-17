package com.example.bankservice.repository;

import com.example.bankservice.domain.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @EntityGraph(attributePaths = {"bank", "bank.address"})
    Optional<Account> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a join fetch a.bank b join fetch b.address where a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    @EntityGraph(attributePaths = {"bank", "bank.address"})
    Page<Account> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"bank", "bank.address"})
    Page<Account> findByBankId(Long bankId, Pageable pageable);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByBankId(Long bankId);
}
