package com.cts.regreportx.controller;

import com.cts.regreportx.model.DataQualityIssue;
import com.cts.regreportx.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    private final ValidationService validationService;

    @Autowired
    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping("/run")
    public ResponseEntity<List<DataQualityIssue>> runValidation() {
        List<DataQualityIssue> issues = validationService.runValidation();
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/issues")
    public ResponseEntity<List<DataQualityIssue>> getIssues() {
        return ResponseEntity.ok(validationService.getAllIssues());
    }
}
