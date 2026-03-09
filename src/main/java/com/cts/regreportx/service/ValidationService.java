package com.cts.regreportx.service;

import com.cts.regreportx.model.DataQualityIssue;
import com.cts.regreportx.model.Loan;
import com.cts.regreportx.repository.DataQualityIssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<DataQualityIssue> runValidation(Integer batchId) {
        List<DataQualityIssue> issues = new ArrayList<>();

        // Example Rule: Loan Amount must be > 0
        List<Loan> loans = sourceDataService.getAllLoans();
        for (Loan loan : loans) {
            if (loan.getLoanAmount() == null || loan.getLoanAmount().signum() <= 0) {
                DataQualityIssue issue = new DataQualityIssue();
                issue.setBatchId(batchId);
                issue.setRuleId(1); // Assuming rule 1 is "Amount > 0"
                issue.setRecordId(loan.getLoanId());
                issue.setMessage("Loan Amount must be greater than zero");
                issue.setSeverity("HIGH");
                issue.setLoggedDate(LocalDateTime.now());
                issue.setStatus("OPEN");

                issues.add(issueRepository.save(issue));
            }
        }

        auditService.logAction(1, "RUN_VALIDATION", "BatchID: " + batchId, "Found " + issues.size() + " issues.");

        return issues;
    }

    public List<DataQualityIssue> getAllExceptions() {
        return issueRepository.findAll();
    }
}
