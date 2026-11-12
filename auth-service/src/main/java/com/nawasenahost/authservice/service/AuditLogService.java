package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.dto.AuditLogPage;
import com.nawasenahost.authservice.dto.AuditLogRequest;
import com.nawasenahost.authservice.entity.AuditLog;

public interface AuditLogService {

    AuditLog save(AuditLogRequest auditLogRequest);
    AuditLogPage search(Integer hotelId, String entityType, String actor, int page, int size);

    // used by auth-service itself (login, user changes)
    void record(String actor, String actorRole, int hotelId, String action, String entityType, String entityId, String details);

}
