package com.nawasenahost.hotelservice.client;

import com.nawasenahost.hotelservice.dto.AuditEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

// hotel-service has no service token, so the token of the user is passed on
@FeignClient(name = "auth-service", contextId = "auditClient")
public interface AuditClient {

    @PostMapping("/api/audit-logs")
    void send(@RequestHeader("Authorization") String authorization,
              @RequestBody AuditEvent auditEvent);

}
