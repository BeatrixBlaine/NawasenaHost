package com.nawasenahost.roomservice.client;

import com.nawasenahost.roomservice.dto.AuditEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// service token with scope audit.write
@FeignClient(name = "auth-service", contextId = "auditClient")
public interface AuditClient {

    @PostMapping("/api/audit-logs")
    void send(@RequestBody AuditEvent auditEvent);

}
