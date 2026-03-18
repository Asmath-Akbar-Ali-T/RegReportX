package com.cts.regreportx.controller;

import com.cts.regreportx.dto.AuditLogDto;
import com.cts.regreportx.model.AuditLog;
import com.cts.regreportx.service.AuditService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuditController(AuditService auditService, ModelMapper modelMapper) {
        this.auditService = auditService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogDto>> getAllAuditLogs() {
        return ResponseEntity.ok(auditService.getAllAuditLogs().stream()
                .map(log -> modelMapper.map(log, AuditLogDto.class))
                .collect(Collectors.toList()));
    }
}
