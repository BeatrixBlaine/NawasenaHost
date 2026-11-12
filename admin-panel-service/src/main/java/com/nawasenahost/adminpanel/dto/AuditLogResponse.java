package com.nawasenahost.adminpanel.dto;

import java.time.LocalDateTime;

public record AuditLogResponse(long auditLogId,
                               LocalDateTime createdAt,
                               String actor,
                               String actorRole,
                               int hotelId,
                               String action,
                               String entityType,
                               String entityId,
                               String details) {
}
