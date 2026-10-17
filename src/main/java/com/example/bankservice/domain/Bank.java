package com.example.bankservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank")
public class Bank extends BaseEntity {

    @Column(name = "bank_name", nullable = false, length = 150)
    private String bankName;

    @Column(name = "ifsc_code", nullable = false, unique = true, length = 11)
    private String ifscCode;

    @Column(name = "branch_name", nullable = false, length = 150)
    private String branchName;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;

    protected Bank() {
    }

    public Bank(String bankName, String ifscCode, String branchName, Address address) {
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.branchName = branchName;
        this.address = address;
    }

    public void update(String bankName, String ifscCode, String branchName) {
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.branchName = branchName;
    }

    public String getBankName() {
        return bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public Address getAddress() {
        return address;
    }
}
