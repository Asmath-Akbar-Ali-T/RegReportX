package com.cts.regreportx.controller;

import com.cts.regreportx.model.RawDataBatch;
import com.cts.regreportx.service.DataIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final DataIngestionService ingestionService;

    @Autowired
    public IngestionController(DataIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/run")
    public ResponseEntity<RawDataBatch> runIngestion() {
        // Mock sourceId for now
        Integer mockSourceId = 1;
        RawDataBatch batch = ingestionService.runIngestion(mockSourceId);
        return ResponseEntity.ok(batch);
    }
}
