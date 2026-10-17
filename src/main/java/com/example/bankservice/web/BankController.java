package com.example.bankservice.web;

import com.example.bankservice.dto.BankRequest;
import com.example.bankservice.dto.BankResponse;
import com.example.bankservice.dto.PageResponse;
import com.example.bankservice.service.BankService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/banks")
@Tag(name = "Banks")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    @Operation(summary = "Create a bank branch with its address")
    public ResponseEntity<BankResponse> create(@Valid @RequestBody BankRequest request) {
        BankResponse created = bankService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a bank by id")
    public BankResponse getById(@PathVariable Long id) {
        return bankService.getById(id);
    }

    @GetMapping("/ifsc/{ifscCode}")
    @Operation(summary = "Get a bank by IFSC code")
    public BankResponse getByIfsc(@PathVariable String ifscCode) {
        return bankService.getByIfsc(ifscCode);
    }

    @GetMapping
    @Operation(summary = "List banks (paginated)")
    public PageResponse<BankResponse> list(
            @PageableDefault(size = 20, sort = "bankName", direction = Sort.Direction.ASC) Pageable pageable) {
        return bankService.list(pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a bank and its address")
    public BankResponse update(@PathVariable Long id, @Valid @RequestBody BankRequest request) {
        return bankService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a bank (only if it has no accounts)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bankService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
