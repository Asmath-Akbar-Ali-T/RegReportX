package com.cts.regreportx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "general_ledger")
public class GeneralLedger {

    @Id
    @Column(name = "\"GLID\"")
    private String glId;

    @Column(name = "\"AccountNumber\"")
    private Long accountNumber;

    @Column(name = "\"BranchID\"")
    private String branchId;

    @Column(name = "\"AccountType\"")
    private String accountType;

    @Column(name = "\"Debit\"")
    private BigDecimal debit;

    @Column(name = "\"Credit\"")
    private BigDecimal credit;

    @Column(name = "\"Currency\"")
    private String currency;

    @Column(name = "\"TransactionDate\"")
    private LocalDate transactionDate;

    public GeneralLedger() {
    }

    public String getGlId() {
        return glId;
    }

    public void setGlId(String glId) {
        this.glId = glId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = debit;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}
