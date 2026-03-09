package com.cts.regreportx.controller;

import com.cts.regreportx.model.DataQualityIssue;
import com.cts.regreportx.model.ExceptionRecord;
import com.cts.regreportx.service.ReportingService;
import com.cts.regreportx.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ValidationController {

    private final ValidationService validationService;
    private final ReportingService reportingService;

    @Autowired
    public ValidationController(ValidationService validationService, ReportingService reportingService) {
        this.validationService = validationService;
        this.reportingService = reportingService;
    }

    @GetMapping("/validation/run")
    public ResponseEntity<List<DataQualityIssue>> runValidation() {
        Integer mockBatchId = 1;
        List<DataQualityIssue> issues = validationService.runValidation(mockBatchId);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/exceptions")
    public ResponseEntity<List<ExceptionRecord>> getAllExceptions() {
        return ResponseEntity.ok(reportingService.getAllExceptions());
    }
}
