package com.orgskills.intelligence.service;

import com.orgskills.intelligence.entity.AuditLog;
import com.orgskills.intelligence.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logEvent(Long actorUserId, String actorEmail, String action, String entityType, String entityId, String details) {
        try {
            AuditLog logEntry = new AuditLog();
            logEntry.setActorUserId(actorUserId);
            logEntry.setActorEmail(actorEmail != null ? actorEmail : "SYSTEM");
            logEntry.setAction(action);
            logEntry.setEntityType(entityType);
            logEntry.setEntityId(entityId);
            logEntry.setDetails(details);
            auditLogRepository.save(logEntry);
        } catch (Exception ex) {
            log.error("Failed to persist audit log: {}", ex.getMessage(), ex);
        }
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }
}
