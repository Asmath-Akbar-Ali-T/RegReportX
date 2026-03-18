package com.cts.regreportx.controller;

import com.cts.regreportx.dto.RawDataBatchDto;
import com.cts.regreportx.model.RawDataBatch;
import com.cts.regreportx.service.DataIngestionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final DataIngestionService ingestionService;
    private final ModelMapper modelMapper;

    @Autowired
    public IngestionController(DataIngestionService ingestionService, ModelMapper modelMapper) {
        this.ingestionService = ingestionService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/run")
    public ResponseEntity<List<RawDataBatchDto>> runIngestion() {
        return ResponseEntity.ok(ingestionService.runIngestion().stream()
                .map(b -> modelMapper.map(b, RawDataBatchDto.class))
                .collect(Collectors.toList()));
    }

    @GetMapping("/batches")
    public ResponseEntity<List<RawDataBatchDto>> getBatches() {
        return ResponseEntity.ok(ingestionService.getAllBatches().stream()
                .map(b -> modelMapper.map(b, RawDataBatchDto.class))
                .collect(Collectors.toList()));
    }
}
