package com.cts.regreportx.controller;

import com.cts.regreportx.dto.DataQualityIssueDto;
import com.cts.regreportx.model.DataQualityIssue;
import com.cts.regreportx.service.ValidationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    private final ValidationService validationService;
    private final ModelMapper modelMapper;

    @Autowired
    public ValidationController(ValidationService validationService, ModelMapper modelMapper) {
        this.validationService = validationService;
        this.modelMapper = modelMapper;
    }

    private DataQualityIssueDto convertToDto(DataQualityIssue issue) {
        return modelMapper.map(issue, DataQualityIssueDto.class);
    }

    @GetMapping("/run")
    public ResponseEntity<List<DataQualityIssueDto>> runValidation() {
        List<DataQualityIssueDto> issues = validationService.runValidation()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/issues")
    public ResponseEntity<List<DataQualityIssueDto>> getIssues() {
        List<DataQualityIssueDto> issues = validationService.getAllIssues()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(issues);
    }
}
