package com.cts.regreportx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "FilingWorkflow")
public class FilingWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WorkflowID")
    private Integer workflowId;

    @Column(name = "ReportID")
    private Integer reportId;

    @Column(name = "StepName")
    private String stepName;

    @Column(name = "ActorID")
    private Integer actorId;

    @Column(name = "StepDate")
    private LocalDateTime stepDate;

    @Column(name = "Status")
    private String status;

    public FilingWorkflow() {
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
