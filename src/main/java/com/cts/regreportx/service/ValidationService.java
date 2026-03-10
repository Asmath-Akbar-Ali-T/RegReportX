package com.cts.regreportx.service;

import com.cts.regreportx.model.*;
import com.cts.regreportx.repository.DataQualityIssueRepository;
import com.cts.regreportx.repository.RawDataBatchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    private final RawRecordService rawRecordService;
    private final RawDataBatchRepository batchRepository;
    private final DataQualityIssueRepository issueRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ValidationService(RawRecordService rawRecordService,
            RawDataBatchRepository batchRepository,
            DataQualityIssueRepository issueRepository,
            AuditService auditService) {
        this.rawRecordService = rawRecordService;
        this.batchRepository = batchRepository;
        this.issueRepository = issueRepository;
        this.auditService = auditService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<DataQualityIssue> runValidation() {
        List<DataQualityIssue> issues = new ArrayList<>();
        List<RawDataBatch> batches = batchRepository.findAll();

        for (RawDataBatch batch : batches) {
            List<RawRecord> records = rawRecordService.getRecordsByBatch(batch.getBatchId());
            Integer sourceId = batch.getSourceId();

            try {
                if (sourceId == 1) { // Loan System
                    for (RawRecord record : records) {
                        Loan loan = objectMapper.readValue(record.getPayloadJson(), Loan.class);
                        // Rule: LoanAmount > 0
                        if (loan.getLoanAmount() == null || loan.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
                            issues.add(createIssue(batch.getBatchId(), 1, record.getRawRecordId().toString(),
                                    "LoanAmount must be > 0", "HIGH"));
                        }
                        // Rule: InterestRate BETWEEN 0 AND 20
                        if (loan.getInterestRate() != null) {
                            BigDecimal rate = loan.getInterestRate();
                            if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(new BigDecimal("20")) > 0) {
                                issues.add(createIssue(batch.getBatchId(), 2, record.getRawRecordId().toString(),
                                        "InterestRate BETWEEN 0 AND 20 failed", "HIGH"));
                            }
                        }
                    }
                    auditService.logAction("RUN_VALIDATION_ON_RAW", "loans (Batch: " + batch.getBatchId() + ")");

                } else if (sourceId == 2) { // Deposit System
                    for (RawRecord record : records) {
                        Deposit deposit = objectMapper.readValue(record.getPayloadJson(), Deposit.class);
                        // Rule: Deposit Amount > 0
                        if (deposit.getAmount() == null || deposit.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                            issues.add(createIssue(batch.getBatchId(), 3, record.getRawRecordId().toString(),
                                    "Deposit Amount > 0 failed", "HIGH"));
                        }
                    }
                    auditService.logAction("RUN_VALIDATION_ON_RAW", "deposits (Batch: " + batch.getBatchId() + ")");

                } else if (sourceId == 4) { // GL System
                    for (RawRecord record : records) {
                        GeneralLedger gl = objectMapper.readValue(record.getPayloadJson(), GeneralLedger.class);
                        // Rule: Debit >= 0
                        if (gl.getDebit() != null && gl.getDebit().compareTo(BigDecimal.ZERO) < 0) {
                            issues.add(createIssue(batch.getBatchId(), 4, record.getRawRecordId().toString(),
                                    "Debit >= 0 failed", "HIGH"));
                        }
                        // Rule: Credit >= 0
                        if (gl.getCredit() != null && gl.getCredit().compareTo(BigDecimal.ZERO) < 0) {
                            issues.add(createIssue(batch.getBatchId(), 5, record.getRawRecordId().toString(),
                                    "Credit >= 0 failed", "HIGH"));
                        }
                    }
                    auditService.logAction("RUN_VALIDATION_ON_RAW",
                            "general_ledger (Batch: " + batch.getBatchId() + ")");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return issues;
    }

    private DataQualityIssue createIssue(Integer batchId, Integer ruleId, String recordId, String message,
            String severity) {
        DataQualityIssue issue = new DataQualityIssue();
        issue.setBatchId(batchId);
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
