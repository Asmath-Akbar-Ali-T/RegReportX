package com.cts.regreportx.service;

import com.cts.regreportx.model.ExceptionRecord;
import com.cts.regreportx.model.FilingWorkflow;
import com.cts.regreportx.model.RegReport;
import com.cts.regreportx.repository.ExceptionRecordRepository;
import com.cts.regreportx.repository.FilingWorkflowRepository;
import com.cts.regreportx.repository.RegReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportingService {

    private final RegReportRepository reportRepository;
    private final FilingWorkflowRepository workflowRepository;
    private final ExceptionRecordRepository exceptionRecordRepository;
    private final RiskCalculationService riskCalculationService;
    private final AuditService auditService;

    @Autowired
    public ReportingService(RegReportRepository reportRepository,
            FilingWorkflowRepository workflowRepository,
            ExceptionRecordRepository exceptionRecordRepository,
            RiskCalculationService riskCalculationService,
            AuditService auditService) {
        this.reportRepository = reportRepository;
        this.workflowRepository = workflowRepository;
        this.exceptionRecordRepository = exceptionRecordRepository;
        this.riskCalculationService = riskCalculationService;
        this.auditService = auditService;
    }

    public RegReport generateReport(Integer templateId, String period) {
        // 1. Create Report
        RegReport report = new RegReport();
        report.setTemplateId(templateId);
        report.setPeriod(period);
        report.setGeneratedDate(LocalDateTime.now());
        report.setStatus("DRAFT");
        report = reportRepository.save(report);

        // 2. Track Workflow Draft Stage
        FilingWorkflow workflow = new FilingWorkflow();
        workflow.setReportId(report.getReportId());
        workflow.setStepName("DRAFT");
        workflow.setActorId(1); // System user
        workflow.setStepDate(LocalDateTime.now());
        workflow.setStatus("COMPLETED");
        workflowRepository.save(workflow);

        // 3. Trigger risk calculations for this report
        riskCalculationService.calculateMetrics(report.getReportId());

        auditService.logAction(1, "GENERATE_REPORT", "TemplateID: " + templateId,
                "Report generated ID: " + report.getReportId());

        return report;
    }

    @Transactional
    public RegReport submitReportForReview(Integer reportId, Integer actorId) {
        return advanceWorkflow(reportId, actorId, "DRAFT", "UNDER_REVIEW");
    }

    @Transactional
    public RegReport approveReport(Integer reportId, Integer actorId, String comments) {
        RegReport report = advanceWorkflow(reportId, actorId, "UNDER_REVIEW", "APPROVED");
        if (comments != null && !comments.trim().isEmpty()) {
            auditService.logAction(actorId, "APPROVE_REPORT_WITH_COMMENTS", "ReportID: " + reportId, "Comments: " + comments);
        }
        return report;
    }

    @Transactional
    public RegReport fileReport(Integer reportId, Integer actorId) {
        return advanceWorkflow(reportId, actorId, "APPROVED", "FILED");
    }

    private RegReport advanceWorkflow(Integer reportId, Integer actorId, String expectedCurrentStatus, String nextStatus) {
        RegReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
        
        if (!expectedCurrentStatus.equals(report.getStatus())) {
            throw new IllegalStateException("Cannot advance report to " + nextStatus + ". Current status is " + report.getStatus() + ", expected " + expectedCurrentStatus);
        }
        
        report.setStatus(nextStatus);
        report = reportRepository.save(report);
        
        FilingWorkflow workflow = new FilingWorkflow();
        workflow.setReportId(reportId);
        workflow.setStepName(nextStatus);
        workflow.setActorId(actorId);
        workflow.setStepDate(LocalDateTime.now());
        workflow.setStatus("COMPLETED");
        workflowRepository.save(workflow);
        
        auditService.logAction(actorId, "WORKFLOW_ADVANCE", "ReportID: " + reportId, "Status changed from " + expectedCurrentStatus + " to " + nextStatus);
        
        return report;
    }

    public Optional<RegReport> getReport(Integer reportId) {
        return reportRepository.findById(reportId);
    }

    public List<RegReport> getAllReports() {
        return reportRepository.findAll();
    }

    public List<ExceptionRecord> getAllExceptions() {
        return exceptionRecordRepository.findAll();
    }
}
