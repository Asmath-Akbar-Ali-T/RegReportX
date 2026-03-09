package com.cts.regreportx.service;

import com.cts.regreportx.model.AuditLog;
import com.cts.regreportx.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog logAction(String action, String resource) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setResource(resource);
        log.setTimestamp(LocalDateTime.now());
        // log.setUserId(1); // Optional depending on how authentication is bound
        return auditLogRepository.save(log);
    }

    // Kept for backward compatibility with other services if needed
    public AuditLog logAction(Integer userId, String action, String resource, String metadata) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setResource(resource);
        log.setTimestamp(LocalDateTime.now());
        log.setMetadata(metadata);
        return auditLogRepository.save(log);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
}
