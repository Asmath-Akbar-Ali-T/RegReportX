package com.cts.regreportx.controller;

import com.cts.regreportx.model.RawDataBatch;
import com.cts.regreportx.service.DataIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final DataIngestionService ingestionService;

    @Autowired
    public IngestionController(DataIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/run")
    public ResponseEntity<List<RawDataBatch>> runIngestion() {
        List<RawDataBatch> batches = ingestionService.runIngestion();
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/batches")
    public ResponseEntity<List<RawDataBatch>> getBatches() {
        return ResponseEntity.ok(ingestionService.getAllBatches());
    }
}
