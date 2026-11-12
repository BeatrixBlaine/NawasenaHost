package com.nawasenahost.customerservice.audit;

import com.nawasenahost.customerservice.client.AuditClient;
import com.nawasenahost.customerservice.dto.AuditEvent;
import com.nawasenahost.customerservice.security.TenantGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

// sends an audit event to auth-service, a failing audit log never breaks the real action
@Component
public class AuditPublisher {

    private static final Logger log = LoggerFactory.getLogger(AuditPublisher.class);

    private final AuditClient auditClient;
    private final TenantGuard tenantGuard;

    public AuditPublisher(AuditClient auditClient,
                          TenantGuard tenantGuard) {
        this.auditClient = auditClient;
        this.tenantGuard = tenantGuard;
    }

    public void publish(int hotelId, String action, String entityType, Object entityId, String details) {

        // the actor is read here, the security context is not available in the async thread
        AuditEvent event = new AuditEvent();
        event.setActor(tenantGuard.currentUsername());
        event.setActorRole(tenantGuard.currentRole());
        event.setHotelId(hotelId);
        event.setAction(action);
        event.setEntityType(entityType);
        event.setEntityId(String.valueOf(entityId));
        event.setDetails(details != null && details.length() > 1000 ? details.substring(0, 1000) : details);

        CompletableFuture.runAsync(() -> {
            try {
                auditClient.send(event);
            } catch (Exception e) {
                log.warn("Audit log could not be sent: {}", e.getMessage());
            }
        });
    }
}
