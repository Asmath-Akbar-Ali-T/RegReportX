package com.cts.regreportx.service;

import com.cts.regreportx.model.ExceptionRecord;
import com.cts.regreportx.model.FilingWorkflow;
import com.cts.regreportx.model.RegReport;
import com.cts.regreportx.repository.ExceptionRecordRepository;
import com.cts.regreportx.repository.FilingWorkflowRepository;
import com.cts.regreportx.repository.RegReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
