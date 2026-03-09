package com.cts.regreportx.service;

import com.cts.regreportx.model.Deposit;
import com.cts.regreportx.model.GeneralLedger;
import com.cts.regreportx.model.Loan;
import com.cts.regreportx.model.RiskMetric;
import com.cts.regreportx.model.TreasuryTrade;
import com.cts.regreportx.repository.RiskMetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RiskCalculationService {

    private final SourceDataService sourceDataService;
    private final RiskMetricRepository riskMetricRepository;
    private final AuditService auditService;

    @Autowired
    public RiskCalculationService(SourceDataService sourceDataService,
            RiskMetricRepository riskMetricRepository,
            AuditService auditService) {
        this.sourceDataService = sourceDataService;
        this.riskMetricRepository = riskMetricRepository;
        this.auditService = auditService;
    }

    public List<RiskMetric> calculateMetrics(Integer reportId) {
        List<RiskMetric> metrics = new ArrayList<>();

        // 1. Total Loans
        BigDecimal totalLoans = sourceDataService.getAllLoans().stream()
                .map(loan -> loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.add(createMetric(reportId, "Total_Loans", totalLoans));

        // 2. Total Deposits
        BigDecimal totalDeposits = sourceDataService.getAllDeposits().stream()
                .map(deposit -> deposit.getAmount() != null ? deposit.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.add(createMetric(reportId, "Total_Deposits", totalDeposits));

        // 3. Treasury Exposure
        BigDecimal totalTreasury = sourceDataService.getAllTreasuryTrades().stream()
                .map(trade -> trade.getNotional() != null ? trade.getNotional() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.add(createMetric(reportId, "Treasury_Exposure", totalTreasury));

        // 4. GL Balances (Net Credit - Debit)
        BigDecimal netGlBalance = sourceDataService.getAllGeneralLedgers().stream()
                .map(gl -> {
                    BigDecimal credit = gl.getCredit() != null ? gl.getCredit() : BigDecimal.ZERO;
                    BigDecimal debit = gl.getDebit() != null ? gl.getDebit() : BigDecimal.ZERO;
                    return credit.subtract(debit);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.add(createMetric(reportId, "Net_GL_Balance", netGlBalance));

        auditService.logAction(1, "CALCULATE_RISK_METRICS", "ReportID: " + reportId, "Calculated 4 metrics.");

        return metrics;
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
