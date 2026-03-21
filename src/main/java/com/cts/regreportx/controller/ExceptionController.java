package com.cts.regreportx.controller;

import com.cts.regreportx.dto.ExceptionResolveRequest;
import com.cts.regreportx.model.ExceptionRecord;
import com.cts.regreportx.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exceptions")
@CrossOrigin(origins = "*")
public class ExceptionController {

    private final ReportingService reportingService;

    @Autowired
    public ExceptionController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/open")
    public ResponseEntity<List<ExceptionRecord>> getOpenExceptions() {
        return ResponseEntity.ok(reportingService.getOpenExceptions());
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveException(@PathVariable Integer id, @RequestBody ExceptionResolveRequest request) {
        try {
            ExceptionRecord resolved = reportingService.resolveException(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Exception resolved successfully and CorrectionLog generated.",
                    "exception", resolved
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/generate/{reportId}")
    public ResponseEntity<Map<String, Object>> generateExceptions(@PathVariable Integer reportId) {
        try {
            List<ExceptionRecord> generated = reportingService.generateExceptionsForReport(reportId);
            return ResponseEntity.ok(Map.of(
                    "message", "Exceptions generated successfully for report ID " + reportId,
                    "count", generated.size(),
                    "exceptions", generated
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
