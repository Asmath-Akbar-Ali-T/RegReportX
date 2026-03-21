package com.cts.regreportx.service;

import com.cts.regreportx.model.ExceptionRecord;
import com.cts.regreportx.model.FilingWorkflow;
import com.cts.regreportx.model.RegReport;
import com.cts.regreportx.repository.ExceptionRecordRepository;
import com.cts.regreportx.repository.FilingWorkflowRepository;
import com.cts.regreportx.repository.RegReportRepository;
import com.cts.regreportx.repository.CorrectionLogRepository;
import com.cts.regreportx.repository.UserRepository;
import com.cts.regreportx.repository.TemplateFieldRepository;
import com.cts.regreportx.repository.RiskMetricRepository;
import com.cts.regreportx.repository.ReportVersionRepository;
import com.cts.regreportx.model.CorrectionLog;
import com.cts.regreportx.model.User;
import com.cts.regreportx.model.TemplateField;
import com.cts.regreportx.model.RiskMetric;
import com.cts.regreportx.model.ReportVersion;
import com.cts.regreportx.dto.ExceptionResolveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CorrectionLogRepository correctionLogRepository;
    private final UserRepository userRepository;
    private final TemplateFieldRepository templateFieldRepository;
    private final RiskMetricRepository riskMetricRepository;
    private final ReportVersionRepository reportVersionRepository;

    @Autowired
    public ReportingService(RegReportRepository reportRepository,
            FilingWorkflowRepository workflowRepository,
            ExceptionRecordRepository exceptionRecordRepository,
            RiskCalculationService riskCalculationService,
            AuditService auditService,
            CorrectionLogRepository correctionLogRepository,
            UserRepository userRepository,
            TemplateFieldRepository templateFieldRepository,
            RiskMetricRepository riskMetricRepository,
            ReportVersionRepository reportVersionRepository) {
        this.reportRepository = reportRepository;
        this.workflowRepository = workflowRepository;
        this.exceptionRecordRepository = exceptionRecordRepository;
        this.riskCalculationService = riskCalculationService;
        this.auditService = auditService;
        this.correctionLogRepository = correctionLogRepository;
        this.userRepository = userRepository;
        this.templateFieldRepository = templateFieldRepository;
        this.riskMetricRepository = riskMetricRepository;
        this.reportVersionRepository = reportVersionRepository;
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

        // 3. Create ReportVersion mapping
        ReportVersion version = new ReportVersion();
        version.setReportId(report.getReportId());
        version.setVersionNumber(1);
        version.setStatus("DRAFT");
        version.setCreatedDate(LocalDateTime.now());
        reportVersionRepository.save(version);

        // 4. Risk calculations are now manually triggered instead of automatically on draft creation.

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
        
        // Update corresponding ReportVersion status
        reportVersionRepository.findTopByReportIdOrderByVersionNumberDesc(reportId)
                .ifPresent(version -> {
                    version.setStatus(nextStatus);
                    reportVersionRepository.save(version);
                });
        
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

    public List<ExceptionRecord> getOpenExceptions() {
        return exceptionRecordRepository.findByStatus("Open");
    }

    @Transactional
    public ExceptionRecord resolveException(Integer exceptionId, ExceptionResolveRequest request) {
        ExceptionRecord exception = exceptionRecordRepository.findById(exceptionId)
                .orElseThrow(() -> new RuntimeException("Exception Record not found: " + exceptionId));

        if ("Resolved".equalsIgnoreCase(exception.getStatus())) {
            throw new RuntimeException("Exception is already resolved");
        }

        exception.setStatus("Resolved");
        ExceptionRecord savedException = exceptionRecordRepository.save(exception);
        
        java.util.concurrent.atomic.AtomicReference<String> oldMetricValue = new java.util.concurrent.atomic.AtomicReference<>(exception.getIssue());

        if (exception.getFieldId() != null && exception.getReportId() != null) {
            templateFieldRepository.findById(exception.getFieldId()).ifPresent(field -> {
                riskMetricRepository.findByReportIdAndMetricName(exception.getReportId(), field.getFieldName())
                        .ifPresent(metric -> {
                            try {
                                if (metric.getMetricValue() != null) {
                                    oldMetricValue.set(metric.getMetricValue().toString());
                                }
                                metric.setMetricValue(new java.math.BigDecimal(request.getCorrectedValue()));
                                riskMetricRepository.save(metric);
                            } catch (Exception ignored) {}
                        });
            });
        }

        CorrectionLog log = new CorrectionLog();
        log.setExceptionId(exception.getExceptionId());
        log.setOldValue(oldMetricValue.get());
        log.setNewValue("Value: " + request.getCorrectedValue() + " | Reason: " + request.getJustification());
        log.setCorrectedDate(LocalDateTime.now());
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                Optional<User> userOpt = userRepository.findByUsername(auth.getName());
                userOpt.ifPresent(user -> log.setCorrectedBy(user.getId().intValue()));
            }
        } catch (Exception e) {}

        correctionLogRepository.save(log);

        auditService.logAction("RESOLVED_REPORT_EXCEPTION", "ExceptionRecord ID: " + exceptionId);

        return savedException;
    }

    @Transactional
    public List<ExceptionRecord> generateExceptionsForReport(Integer reportId) {
        List<ExceptionRecord> generated = new java.util.ArrayList<>();
        List<RiskMetric> metrics = riskMetricRepository.findByReportId(reportId);
        if(metrics == null || metrics.isEmpty()) return generated;

        for (RiskMetric metric : metrics) {
            String name = metric.getMetricName();
            java.math.BigDecimal value = metric.getMetricValue();
            if (value == null) continue;

            boolean breached = false;
            String issueMsg = "";
            String severity = "High";

            if ("CRAR".equalsIgnoreCase(name) && value.compareTo(new java.math.BigDecimal("9")) < 0) {
                breached = true;
                issueMsg = "Capital breach: CRAR is below 9% threshold (" + value + "%)";
            } else if ("LCR".equalsIgnoreCase(name) && value.compareTo(new java.math.BigDecimal("100")) < 0) {
                breached = true;
                issueMsg = "Liquidity deficit: LCR is below 100% threshold (" + value + "%)";
            } else if ("Loan_to_Deposit_Ratio".equalsIgnoreCase(name) && value.compareTo(new java.math.BigDecimal("90")) > 0) {
                breached = true;
                issueMsg = "High lending risk: Loan-to-Deposit Ratio exceeds 90% (" + value + "%)";
                severity = "Medium";
            } else if ("Net_GL_Balance".equalsIgnoreCase(name) && value.compareTo(java.math.BigDecimal.ZERO) < 0) {
                breached = true;
                issueMsg = "Solvency breach: Net GL Balance evaluates to negative (" + value + ")";
                severity = "Critical";
            }

            if (breached) {
                Integer fieldId = null;
                List<TemplateField> fields = templateFieldRepository.findByFieldName(name);
                if (fields != null && !fields.isEmpty()) {
                    fieldId = fields.get(0).getFieldId();
                }

                ExceptionRecord ex = new ExceptionRecord();
                ex.setReportId(reportId);
                ex.setFieldId(fieldId);
                ex.setIssue(issueMsg);
                ex.setSeverity(severity);
                ex.setStatus("Open");
                generated.add(exceptionRecordRepository.save(ex));
            }
        }
        auditService.logAction(1, "GENERATE_REPORT_EXCEPTIONS", "ReportID: " + reportId, "Generated " + generated.size() + " exceptions");
        return generated;
    }
}
