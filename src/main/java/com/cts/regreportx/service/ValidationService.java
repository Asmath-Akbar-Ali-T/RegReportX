package com.cts.regreportx.service;

import com.cts.regreportx.model.DataQualityIssue;
import com.cts.regreportx.model.Deposit;
import com.cts.regreportx.model.GeneralLedger;
import com.cts.regreportx.model.Loan;
import com.cts.regreportx.repository.DataQualityIssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    private final SourceDataService sourceDataService;
    private final DataQualityIssueRepository issueRepository;
    private final AuditService auditService;

    @Autowired
    public ValidationService(SourceDataService sourceDataService,
            DataQualityIssueRepository issueRepository,
            AuditService auditService) {
        this.sourceDataService = sourceDataService;
        this.issueRepository = issueRepository;
        this.auditService = auditService;
    }

    public List<DataQualityIssue> runValidation() {
        List<DataQualityIssue> issues = new ArrayList<>();

        // 1. Validate Loans
        List<Loan> loans = sourceDataService.getAllLoans();
        for (Loan loan : loans) {
            boolean hasIssue = false;
            // Rule: LoanAmount > 0
            if (loan.getLoanAmount() == null || loan.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
                issues.add(createIssue(1, loan.getLoanId(), "LoanAmount must be > 0", "HIGH"));
                hasIssue = true;
            }
            // Rule: InterestRate BETWEEN 0 AND 20
            if (loan.getInterestRate() != null) {
                BigDecimal rate = loan.getInterestRate();
                if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(new BigDecimal("20")) > 0) {
                    issues.add(createIssue(2, loan.getLoanId(), "InterestRate BETWEEN 0 AND 20 failed", "HIGH"));
                    hasIssue = true;
                }
            }
        }
        auditService.logAction("RUN_VALIDATION", "loans");

        // 2. Validate Deposits
        List<Deposit> deposits = sourceDataService.getAllDeposits();
        for (Deposit deposit : deposits) {
            // Rule: Deposit Amount > 0
            if (deposit.getAmount() == null || deposit.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                issues.add(createIssue(3, deposit.getDepositId(), "Deposit Amount > 0 failed", "HIGH"));
            }
        }
        auditService.logAction("RUN_VALIDATION", "deposits");

        // 3. Validate General Ledger
        List<GeneralLedger> gls = sourceDataService.getAllGeneralLedgers();
        for (GeneralLedger gl : gls) {
            // Rule: Debit >= 0
            if (gl.getDebit() != null && gl.getDebit().compareTo(BigDecimal.ZERO) < 0) {
                issues.add(createIssue(4, gl.getGlId(), "Debit >= 0 failed", "HIGH"));
            }
            // Rule: Credit >= 0
            if (gl.getCredit() != null && gl.getCredit().compareTo(BigDecimal.ZERO) < 0) {
                issues.add(createIssue(5, gl.getGlId(), "Credit >= 0 failed", "HIGH"));
            }
        }
        auditService.logAction("RUN_VALIDATION", "general_ledger");

        return issues;
    }

    private DataQualityIssue createIssue(Integer ruleId, String recordId, String message, String severity) {
        DataQualityIssue issue = new DataQualityIssue();
        issue.setRuleId(ruleId);
        issue.setRecordId(recordId);
        issue.setMessage(message);
        issue.setSeverity(severity);
        issue.setLoggedDate(LocalDateTime.now());
        issue.setStatus("Open");
        return issueRepository.save(issue);
    }

    public List<DataQualityIssue> getAllIssues() {
        return issueRepository.findAll();
    }
}
