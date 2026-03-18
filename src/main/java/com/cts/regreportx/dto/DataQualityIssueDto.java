package com.cts.regreportx.dto;

import java.time.LocalDateTime;

public class DataQualityIssueDto {

    private Integer issueId;

    private Integer batchId;

    private Integer ruleId;

    private String recordId;

    private String message;

    private String severity;

    private LocalDateTime loggedDate;

    private String status;

    public DataQualityIssueDto() {
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
