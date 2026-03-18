package com.cts.regreportx.dto;

import java.time.LocalDateTime;

public class FilingWorkflowDto {

    private Integer workflowId;

    private Integer reportId;

    private String stepName;

    private Integer actorId;

    private LocalDateTime stepDate;

    private String status;

    public FilingWorkflowDto() {
    }

    public Integer getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Integer workflowId) {
        this.workflowId = workflowId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Integer getActorId() {
        return actorId;
    }

    public void setActorId(Integer actorId) {
        this.actorId = actorId;
    }

    public LocalDateTime getStepDate() {
        return stepDate;
    }

    public void setStepDate(LocalDateTime stepDate) {
        this.stepDate = stepDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
