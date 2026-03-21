package com.cts.regreportx.service;

import com.cts.regreportx.component.DynamicRiskEvaluator;
import com.cts.regreportx.model.*;
import com.cts.regreportx.repository.NotificationRepository;
import com.cts.regreportx.repository.RegReportRepository;
import com.cts.regreportx.repository.RiskMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RiskCalculationService {

    private final SourceDataService sourceDataService;
    private final RiskMetricRepository riskMetricRepository;
    private final NotificationRepository notificationRepository;
    private final AuditService auditService;
    private final DynamicRiskEvaluator riskEvaluator;
    private final TemplateService templateService;
    private final RegReportRepository reportRepository;

    @Autowired
    public RiskCalculationService(SourceDataService sourceDataService,
                                  RiskMetricRepository riskMetricRepository,
                                  NotificationRepository notificationRepository,
                                  AuditService auditService,
                                  DynamicRiskEvaluator riskEvaluator,
                                  TemplateService templateService,
                                  RegReportRepository reportRepository) {
        this.sourceDataService = sourceDataService;
        this.riskMetricRepository = riskMetricRepository;
        this.notificationRepository = notificationRepository;
        this.auditService = auditService;
        this.riskEvaluator = riskEvaluator;
        this.templateService = templateService;
        this.reportRepository = reportRepository;
    }

    public List<RiskMetric> calculateMetrics(Integer reportId) {
        List<RiskMetric> metrics = new ArrayList<>();
        riskMetricRepository.deleteByReportId(reportId);

        RegReport report = reportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("Report Not Found"));
        List<TemplateField> fields = templateService.getFieldsByTemplateId(report.getTemplateId());

        if (fields.isEmpty()) {
            auditService.logAction(1, "CALCULATE_RISK_METRICS_SKIPPED", "ReportID: " + reportId, "No template fields found to calculate.");
            return metrics;
        }

        // Fetch Data
        List<Loan> loans = sourceDataService.getAllLoans();
        List<Deposit> deposits = sourceDataService.getAllDeposits();
        List<TreasuryTrade> treasuryTrades = sourceDataService.getAllTreasuryTrades();
        List<GeneralLedger> generalLedgers = sourceDataService.getAllGeneralLedgers();

        // 1. Build Base Aggregations Context
        Map<String, BigDecimal> context = new HashMap<>();

        BigDecimal totalLoans = loans.stream().map(l -> l.getLoanAmount() != null ? l.getLoanAmount() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        context.put("Total_Loans", totalLoans);

        BigDecimal totalDeposits = deposits.stream().map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        context.put("Total_Deposits", totalDeposits);

        BigDecimal cdRatio = BigDecimal.ZERO;
        if (totalDeposits.compareTo(BigDecimal.ZERO) > 0) {
            cdRatio = totalLoans.divide(totalDeposits, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }
        context.put("Loan_to_Deposit_Ratio", cdRatio);

        BigDecimal totalTreasury = treasuryTrades.stream().map(t -> t.getNotional() != null ? t.getNotional() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        context.put("Treasury_Exposure", totalTreasury);

        BigDecimal netGlBalance = generalLedgers.stream().map(gl -> {
            BigDecimal credit = gl.getCredit() != null ? gl.getCredit() : BigDecimal.ZERO;
            BigDecimal debit = gl.getDebit() != null ? gl.getDebit() : BigDecimal.ZERO;
            return credit.subtract(debit);
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        context.put("Net_GL_Balance", netGlBalance);

        BigDecimal rwa = loans.stream().map(loan -> {
            BigDecimal amt = loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO;
            return amt.multiply(getRiskWeight(loan.getLoanType()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        context.put("RWA", rwa);

        BigDecimal maxExposure = BigDecimal.ZERO;
        if (netGlBalance.abs().compareTo(BigDecimal.ZERO) > 0) {
            Map<Integer, BigDecimal> exposureByCustomer = loans.stream().filter(l -> l.getCustomerId() != null)
                    .collect(Collectors.groupingBy(Loan::getCustomerId, Collectors.reducing(BigDecimal.ZERO, l -> l.getLoanAmount() != null ? l.getLoanAmount() : BigDecimal.ZERO, BigDecimal::add)));
            for (BigDecimal exp : exposureByCustomer.values()) {
                if (exp.compareTo(maxExposure) > 0) maxExposure = exp;
            }
        }
        context.put("MAX(Customer_Total_Load)", maxExposure);

        // 2. Evaluate Dynamic Template Fields
        for (TemplateField field : fields) {
            String formula = field.getMappingExpression();
            if (formula == null || formula.isEmpty()) continue;

            // Evaluate 
            BigDecimal calculatedValue = riskEvaluator.evaluateFormula(formula, context);
            
            // Add to Context (so sequential formulas can chain off previous results)
            context.put(field.getFieldName(), calculatedValue);

            // Save
            metrics.add(createMetric(reportId, field.getFieldName(), calculatedValue));
        }

        // 3. Dynamic Alerts / Threshold Checks (Based on what reached the context map)
        if (context.containsKey("CRAR") && context.get("CRAR").compareTo(new BigDecimal("9")) < 0) {
            generateRiskAlert("Capital breach: CRAR is below 9% threshold (" + context.get("CRAR") + "%) for Report ID " + reportId);
        }
        if (context.containsKey("LCR") && context.get("LCR").compareTo(new BigDecimal("100")) < 0) {
            generateRiskAlert("Liquidity breach: LCR is below 100% threshold (" + context.get("LCR") + "%) for Report ID " + reportId);
        }
        if (context.containsKey("Loan_to_Deposit_Ratio") && context.get("Loan_to_Deposit_Ratio").compareTo(new BigDecimal("90")) > 0) {
            generateRiskAlert("High lending risk: Loan-to-Deposit Ratio exceeds safe limit (" + context.get("Loan_to_Deposit_Ratio") + "%) for Report ID " + reportId);
        }

        auditService.logAction(1, "CALCULATE_RISK_METRICS", "ReportID: " + reportId, "Dynamically calculated " + metrics.size() + " metrics.");
        return metrics;
    }

    private BigDecimal getRiskWeight(String loanType) {
        if (loanType == null) return new BigDecimal("1.00");
        switch (loanType.toLowerCase()) {
            case "home loan": return new BigDecimal("0.50");
            case "personal loan":
            case "auto loan": return new BigDecimal("0.75");
            case "corporate loan": default: return new BigDecimal("1.00");
        }
    }

    private void generateRiskAlert(String message) {
        Notification notification = new Notification();
        notification.setUserId(1); 
        notification.setMessage(message);
        notification.setCategory("Risk");
        notification.setStatus("UNREAD");
        notification.setCreatedDate(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private RiskMetric createMetric(Integer reportId, String name, BigDecimal value) {
        RiskMetric metric = new RiskMetric();
        metric.setReportId(reportId);
        metric.setMetricName(name);
        metric.setMetricValue(value);
        metric.setCalculationDate(LocalDateTime.now());
        return riskMetricRepository.save(metric);
    }

    public List<RiskMetric> getAllMetrics() {
        return riskMetricRepository.findAll();
    }
}
