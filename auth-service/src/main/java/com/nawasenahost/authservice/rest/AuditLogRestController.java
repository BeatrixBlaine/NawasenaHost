package com.nawasenahost.authservice.rest;

import com.nawasenahost.authservice.dto.AuditLogPage;
import com.nawasenahost.authservice.dto.AuditLogRequest;
import com.nawasenahost.authservice.entity.AuditLog;
import com.nawasenahost.authservice.service.AuditLogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuditLogRestController {

    private final AuditLogService auditLogService;

    public AuditLogRestController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // called by the other services (service token, scope audit.write)
    @PostMapping("/audit-logs")
    public AuditLog addAuditLog(@Valid @RequestBody AuditLogRequest auditLogRequest) {
        return auditLogService.save(auditLogRequest);
    }

    // ADMIN: every hotel, MANAGER: own hotel
    @GetMapping("/audit-logs")
    public AuditLogPage getAuditLogs(@RequestParam(required = false) Integer hotelId,
                                     @RequestParam(required = false) String entityType,
                                     @RequestParam(required = false) String actor,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "25") int size) {
        return auditLogService.search(hotelId, entityType, actor, page, size);
    }
}
