package com.cts.regreportx.controller;

import com.cts.regreportx.model.RegReport;
import com.cts.regreportx.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<RegReport> generateReport(
            @RequestParam(required = false, defaultValue = "1") Integer templateId,
            @RequestParam(required = false, defaultValue = "2026-Q1") String period) {
        RegReport report = reportingService.generateReport(templateId, period);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegReport> getReport(@PathVariable Integer id) {
        return reportingService.getReport(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<?> submitReport(@PathVariable Integer id, @RequestParam(defaultValue = "1") Integer actorId) {
        try {
            RegReport report = reportingService.submitReportForReview(id, actorId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveReport(@PathVariable Integer id, 
                                           @RequestParam(defaultValue = "1") Integer actorId, 
                                           @RequestParam(required = false) String comments) {
        try {
            RegReport report = reportingService.approveReport(id, actorId, comments);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/file")
    public ResponseEntity<?> fileReport(@PathVariable Integer id, @RequestParam(defaultValue = "1") Integer actorId) {
        try {
            RegReport report = reportingService.fileReport(id, actorId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
