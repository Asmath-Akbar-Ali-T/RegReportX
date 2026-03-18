package com.cts.regreportx.controller;

import com.cts.regreportx.dto.RiskMetricDto;
import com.cts.regreportx.model.RiskMetric;
import com.cts.regreportx.service.RiskCalculationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    private final RiskCalculationService riskCalculationService;
    private final ModelMapper modelMapper;

    @Autowired
    public RiskController(RiskCalculationService riskCalculationService, ModelMapper modelMapper) {
        this.riskCalculationService = riskCalculationService;
        this.modelMapper = modelMapper;
    }

    private RiskMetricDto convertToDto(RiskMetric metric) {
        return modelMapper.map(metric, RiskMetricDto.class);
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<RiskMetricDto>> getMetrics() {
        List<RiskMetricDto> dtoList = riskCalculationService.getAllMetrics()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/calculate/{reportId}")
    public ResponseEntity<Map<String, Object>> calculateMetrics(@PathVariable Integer reportId) {
        List<RiskMetric> metrics = riskCalculationService.calculateMetrics(reportId);
        
        List<String> metricNames = metrics.stream()
                .map(RiskMetric::getMetricName)
                .collect(Collectors.toList());
                
        Map<String, Object> response = new HashMap<>();
        response.put("reportId", reportId);
        response.put("metricsCalculated", metricNames);
        
        return ResponseEntity.ok(response);
    }
}
