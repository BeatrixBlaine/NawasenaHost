package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.AuditLogPageResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final GatewayClient gatewayClient;

    public AuditLogServiceImpl(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @Override
    public AuditLogPageResponse search(SessionData sessionData, Integer hotelId, String entityType, String actor, int page) {
        return gatewayClient.get(sessionData, AuditLogPageResponse.class,
                "/api/audit-logs?hotelId={hotelId}&entityType={entityType}&actor={actor}&page={page}&size=25",
                hotelId == null ? "" : hotelId,
                entityType == null ? "" : entityType,
                actor == null ? "" : actor,
                page);
    }
}
