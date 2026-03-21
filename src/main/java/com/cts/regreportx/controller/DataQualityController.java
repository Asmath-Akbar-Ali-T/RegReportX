package com.cts.regreportx.controller;

import com.cts.regreportx.dto.DataQualityResolveRequest;
import com.cts.regreportx.model.DataQualityIssue;
import com.cts.regreportx.service.DataQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/data-quality")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('COMPLIANCE_ANALYST', 'REGTECH_ADMIN')")
public class DataQualityController {

    private final DataQualityService dataQualityService;

    @Autowired
    public DataQualityController(DataQualityService dataQualityService) {
        this.dataQualityService = dataQualityService;
    }

    @GetMapping("/issues")
    public ResponseEntity<List<DataQualityIssue>> getOpenIssues() {
        List<DataQualityIssue> issues = dataQualityService.getOpenIssues();
        return ResponseEntity.ok(issues);
    }

    @PutMapping("/issues/{id}/resolve")
    public ResponseEntity<?> resolveIssue(@PathVariable Integer id, @RequestBody DataQualityResolveRequest request) {
        try {
            java.util.Map<String, Object> resolvedData = dataQualityService.resolveIssue(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Issue resolved successfully and CorrectionLog generated.",
                    "issue", resolvedData.get("issue"),
                    "patchedRecordPayload", resolvedData.get("patchedRecordPayload")
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
