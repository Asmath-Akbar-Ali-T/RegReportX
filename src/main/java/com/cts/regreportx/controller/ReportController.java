package com.cts.regreportx.controller;

import com.cts.regreportx.model.RegReport;
import com.cts.regreportx.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportingService reportingService;

    @Autowired
    public ReportController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @PostMapping("/generate")
    public ResponseEntity<RegReport> generateReport() {
        // Mocking template and period
        Integer mockTemplateId = 1;
        String period = "2026-Q1";
        RegReport report = reportingService.generateReport(mockTemplateId, period);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegReport> getReport(@PathVariable Integer id) {
        return reportingService.getReport(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
