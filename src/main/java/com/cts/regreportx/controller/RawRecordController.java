package com.cts.regreportx.controller;

import com.cts.regreportx.model.RawRecord;
import com.cts.regreportx.service.RawRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/raw-records")
@PreAuthorize("hasAnyRole('OPERATIONS_OFFICER', 'REGTECH_ADMIN')")
public class RawRecordController {

    private final RawRecordService rawRecordService;

    @Autowired
    public RawRecordController(RawRecordService rawRecordService) {
        this.rawRecordService = rawRecordService;
    }

    @PostMapping("/load/{batchId}")
    public ResponseEntity<Map<String, Object>> loadRawRecords(@PathVariable Integer batchId) {
        int recordsInserted = rawRecordService.loadRawRecords(batchId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Raw records loaded successfully");
        response.put("recordsInserted", recordsInserted);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<RawRecord>> getRecordsByBatch(@PathVariable Integer batchId) {
        return ResponseEntity.ok(rawRecordService.getRecordsByBatch(batchId));
    }
}
