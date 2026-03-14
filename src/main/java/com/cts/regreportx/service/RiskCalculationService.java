package com.cts.regreportx.service;

import com.cts.regreportx.model.Deposit;
import com.cts.regreportx.model.GeneralLedger;
import com.cts.regreportx.model.Loan;
import com.cts.regreportx.model.RiskMetric;
import com.cts.regreportx.model.TreasuryTrade;
import com.cts.regreportx.model.Notification;
import com.cts.regreportx.repository.RiskMetricRepository;
import com.cts.regreportx.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RiskCalculationService {

    private final SourceDataService sourceDataService;
    private final RiskMetricRepository riskMetricRepository;
    private final NotificationRepository notificationRepository;
    private final AuditService auditService;

    @Autowired
    public RiskCalculationService(SourceDataService sourceDataService,
            RiskMetricRepository riskMetricRepository,
            NotificationRepository notificationRepository,
            AuditService auditService) {
        this.sourceDataService = sourceDataService;
        this.riskMetricRepository = riskMetricRepository;
        this.notificationRepository = notificationRepository;
        this.auditService = auditService;
    }

    public List<RiskMetric> calculateMetrics(Integer reportId) {
        List<RiskMetric> metrics = new ArrayList<>();

        // Clear existing metrics to prevent duplicates
        riskMetricRepository.deleteByReportId(reportId);

        List<Loan> loans = sourceDataService.getAllLoans();
        List<Deposit> deposits = sourceDataService.getAllDeposits();
        List<TreasuryTrade> treasuryTrades = sourceDataService.getAllTreasuryTrades();
        List<GeneralLedger> generalLedgers = sourceDataService.getAllGeneralLedgers();

        // --- BASE METRICS ---

        // 1. Total Loans
        BigDecimal totalLoans = loans.stream()
                .map(loan -> loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrics.add(createMetric(reportId, "Total_Loans", totalLoans));

        // 2. Total Deposits
        BigDecimal totalDeposits = deposits.stream()
                .map(deposit -> deposit.getAmount() != null ? deposit.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrics.add(createMetric(reportId, "Total_Deposits", totalDeposits));

        // 3. Treasury Exposure (HQLA)
        BigDecimal totalTreasury = treasuryTrades.stream()
                .map(trade -> trade.getNotional() != null ? trade.getNotional() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrics.add(createMetric(reportId, "Treasury_Exposure", totalTreasury));

        // 4. Net GL Balance
        BigDecimal netGlBalance = generalLedgers.stream()
                .map(gl -> {
                    BigDecimal credit = gl.getCredit() != null ? gl.getCredit() : BigDecimal.ZERO;
                    BigDecimal debit = gl.getDebit() != null ? gl.getDebit() : BigDecimal.ZERO;
                    System.out.println("Credit: " + credit + ", Debit: " + debit);
                    return credit.subtract(debit);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrics.add(createMetric(reportId, "Net_GL_Balance", netGlBalance));

        // --- DERIVED METRICS ---

        // 5. Risk Weighted Assets (RWA)
        BigDecimal rwa = loans.stream()
                .map(loan -> {
                    BigDecimal amount = loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO;
                    BigDecimal weight = getRiskWeight(loan.getLoanType());
                    return amount.multiply(weight);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrics.add(createMetric(reportId, "RWA", rwa));

        // Total Capital is now Absolute value of Net GL Balance
        BigDecimal totalCapital = netGlBalance.abs();

        // 6. Capital Adequacy Ratio (CRAR)
        BigDecimal crar = BigDecimal.ZERO;
        if (rwa.compareTo(BigDecimal.ZERO) > 0) {
            crar = totalCapital.multiply(new BigDecimal("100")).divide(rwa, 4, RoundingMode.HALF_UP);
        }
        metrics.add(createMetric(reportId, "CRAR", crar));

        // Threshold check for CRAR
        if (crar.compareTo(new BigDecimal("9")) < 0) {
            generateRiskAlert("Capital breach: CRAR is below 9% threshold (" + crar + "%) for Report ID " + reportId);
        }

        // 7. Liquidity Coverage Ratio (LCR)
        BigDecimal expectedWithdrawal = totalDeposits.multiply(new BigDecimal("0.10"));
        BigDecimal lcr = BigDecimal.ZERO;
        if (expectedWithdrawal.compareTo(BigDecimal.ZERO) > 0) {
            lcr = totalTreasury.multiply(new BigDecimal("100")).divide(expectedWithdrawal, 4, RoundingMode.HALF_UP);
        }
        metrics.add(createMetric(reportId, "LCR", lcr));

        // Threshold check for LCR
        if (lcr.compareTo(new BigDecimal("100")) < 0) {
            generateRiskAlert("Liquidity breach: LCR is below 100% threshold (" + lcr + "%) for Report ID " + reportId);
        }

        // 8. Loan to Deposit Ratio (LDR)
        BigDecimal ldr = BigDecimal.ZERO;
        if (totalDeposits.compareTo(BigDecimal.ZERO) > 0) {
            ldr = totalLoans.multiply(new BigDecimal("100")).divide(totalDeposits, 4, RoundingMode.HALF_UP);
        }
        metrics.add(createMetric(reportId, "Loan_to_Deposit_Ratio", ldr));

        // Threshold check for LDR
        if (ldr.compareTo(new BigDecimal("90")) > 0) {
            generateRiskAlert("High lending risk: Loan-to-Deposit Ratio exceeds safe limit (" + ldr
                    + "%) for Report ID " + reportId);
        }

        // 9. Exposure Concentration
        if (totalCapital.compareTo(BigDecimal.ZERO) > 0) {
            Map<Integer, BigDecimal> exposureByCustomer = loans.stream()
                    .filter(loan -> loan.getCustomerId() != null)
                    .collect(Collectors.groupingBy(
                            Loan::getCustomerId,
                            Collectors.reducing(BigDecimal.ZERO,
                                    loan -> loan.getLoanAmount() != null ? loan.getLoanAmount() : BigDecimal.ZERO,
                                    BigDecimal::add)));

            BigDecimal maxExposurePercent = BigDecimal.ZERO;
            for (Map.Entry<Integer, BigDecimal> entry : exposureByCustomer.entrySet()) {
                BigDecimal exposurePercent = entry.getValue()
                        .multiply(new BigDecimal("100"))
                        .divide(totalCapital, 4, RoundingMode.HALF_UP);

                if (exposurePercent.compareTo(maxExposurePercent) > 0) {
                    maxExposurePercent = exposurePercent;
                }

                if (exposurePercent.compareTo(new BigDecimal("20")) > 0) {
                    generateRiskAlert("Concentration breach: Customer " + entry.getKey() +
                            " exposure exceeds 20% of capital (" + exposurePercent.setScale(2, RoundingMode.HALF_UP)
                            + "%) for Report ID " + reportId);
                }
            }
            metrics.add(createMetric(reportId, "Exposure_Concentration", maxExposurePercent));
        } else {
            metrics.add(createMetric(reportId, "Exposure_Concentration", BigDecimal.ZERO));
        }

        // 10. Liquidity Buffer
        BigDecimal liquidityBuffer = totalTreasury.subtract(expectedWithdrawal);
        metrics.add(createMetric(reportId, "Liquidity_Buffer", liquidityBuffer));

        // 11. Net Balance Sheet Position
        BigDecimal netAssets = totalLoans.add(totalTreasury);
        BigDecimal netLiabilities = totalDeposits;
        metrics.add(createMetric(reportId, "NetAssets", netAssets));
        metrics.add(createMetric(reportId, "NetLiabilities", netLiabilities));

        auditService.logAction(1, "CALCULATE_RISK_METRICS", "ReportID: " + reportId, "Calculated 11 metrics.");

        return metrics;
    }

    private BigDecimal getRiskWeight(String loanType) {
        if (loanType == null)
            return new BigDecimal("1.00"); // Default 100%
        switch (loanType.toLowerCase()) {
            case "home loan":
                return new BigDecimal("0.50");
            case "personal loan":
            case "auto loan":
                return new BigDecimal("0.75");
            case "corporate loan":
            default:
                return new BigDecimal("1.00");
        }
    }

    private void generateRiskAlert(String message) {
        Notification notification = new Notification();
        notification.setUserId(1); // System Admin / Risk Officer
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
        // Round to 2 decimal places before saving
        metric.setMetricValue(value.setScale(2, RoundingMode.HALF_UP));
        metric.setCalculationDate(LocalDateTime.now());
        return riskMetricRepository.save(metric);
    }

    public List<RiskMetric> getAllMetrics() {
        return riskMetricRepository.findAll();
    }
}
