package com.example.bankservice.web;

import com.example.bankservice.dto.AccountCreateRequest;
import com.example.bankservice.dto.AccountResponse;
import com.example.bankservice.dto.AccountUpdateRequest;
import com.example.bankservice.dto.AmountRequest;
import com.example.bankservice.dto.PageResponse;
import com.example.bankservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @Operation(summary = "Open a new account at a bank")
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountCreateRequest request) {
        AccountResponse created = accountService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{accountNumber}").buildAndExpand(created.accountNumber()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get an account by account number")
    public AccountResponse get(@PathVariable String accountNumber) {
        return accountService.getByAccountNumber(accountNumber);
    }

    @GetMapping
    @Operation(summary = "List accounts, optionally filtered by bank (paginated)")
    public PageResponse<AccountResponse> list(
            @RequestParam(required = false) Long bankId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return accountService.list(bankId, pageable);
    }

    @PutMapping("/{accountNumber}")
    @Operation(summary = "Update account holder name and account type")
    public AccountResponse update(@PathVariable String accountNumber,
                                  @Valid @RequestBody AccountUpdateRequest request) {
        return accountService.update(accountNumber, request);
    }

    @PostMapping("/{accountNumber}/deposit")
    @Operation(summary = "Deposit money into an account")
    public AccountResponse deposit(@PathVariable String accountNumber,
                                   @Valid @RequestBody AmountRequest request) {
        return accountService.deposit(accountNumber, request.amount());
    }

    @PostMapping("/{accountNumber}/withdraw")
    @Operation(summary = "Withdraw money from an account")
    public AccountResponse withdraw(@PathVariable String accountNumber,
                                    @Valid @RequestBody AmountRequest request) {
        return accountService.withdraw(accountNumber, request.amount());
    }

    @DeleteMapping("/{accountNumber}")
    @Operation(summary = "Close (delete) an account")
    public ResponseEntity<Void> delete(@PathVariable String accountNumber) {
        accountService.delete(accountNumber);
        return ResponseEntity.noContent().build();
    }
}
