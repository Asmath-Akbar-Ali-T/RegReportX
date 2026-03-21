package com.cts.regreportx.service;

import com.cts.regreportx.model.AuditLog;
import com.cts.regreportx.model.User;
import com.cts.regreportx.repository.AuditLogRepository;
import com.cts.regreportx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public AuditLog logAction(String action, String resource) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setResource(resource);
        log.setTimestamp(LocalDateTime.now());
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                String username = auth.getName();
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent()) {
                    log.setUserId(userOpt.get().getId().intValue());
                } else {
                    Optional<User> emailOpt = userRepository.findByEmail(username);
                    emailOpt.ifPresent(user -> log.setUserId(user.getId().intValue()));
                }
            }
        } catch (Exception e) {
            // If security context is unavailable (e.g. system startup jobs), fail gracefully
        }

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
