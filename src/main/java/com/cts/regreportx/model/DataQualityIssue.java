package com.cts.regreportx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "DataQualityIssue")
public class DataQualityIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IssueID")
    private Integer issueId;

    @Column(name = "BatchID")
    private Integer batchId;

    @Column(name = "RuleID")
    private Integer ruleId;

    @Column(name = "RecordID")
    private String recordId;

    @Column(name = "Message")
    private String message;

    @Column(name = "Severity")
    private String severity;

    @Column(name = "LoggedDate")
    private LocalDateTime loggedDate;

    @Column(name = "Status")
    private String status;

    public DataQualityIssue() {
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDateTime getLoggedDate() {
        return loggedDate;
    }

    public void setLoggedDate(LocalDateTime loggedDate) {
        this.loggedDate = loggedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
