package com.cts.regreportx.controller;

import com.cts.regreportx.model.RiskMetric;
import com.cts.regreportx.service.RiskCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/risk")
@PreAuthorize("hasAnyRole('RISK_ANALYST', 'REGTECH_ADMIN')")
public class RiskController {

    private final RiskCalculationService riskCalculationService;

    @Autowired
    public RiskController(RiskCalculationService riskCalculationService) {
        this.riskCalculationService = riskCalculationService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<RiskMetric>> getMetrics() {
        return ResponseEntity.ok(riskCalculationService.getAllMetrics());
    }

    @PostMapping("/calculate/{reportId}")
    public ResponseEntity<Map<String, Object>> calculateMetrics(@PathVariable Integer reportId) {
        List<RiskMetric> metrics = riskCalculationService.calculateMetrics(reportId);

        List<String> metricNames = new java.util.ArrayList<>();
        for (RiskMetric metric : metrics) {
            metricNames.add(metric.getMetricName());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("reportId", reportId);
        response.put("metricsCalculated", metricNames);

        return ResponseEntity.ok(response);
    }
}
