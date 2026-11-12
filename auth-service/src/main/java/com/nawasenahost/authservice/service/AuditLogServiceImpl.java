package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.dto.AuditLogPage;
import com.nawasenahost.authservice.dto.AuditLogRequest;
import com.nawasenahost.authservice.entity.AuditLog;
import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.entity.AuthRole;
import com.nawasenahost.authservice.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository,
                               CurrentUserService currentUserService) {
        this.auditLogRepository = auditLogRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public AuditLog save(AuditLogRequest auditLogRequest) {

        AuditLog auditLog = new AuditLog();
        auditLog.setCreatedAt(LocalDateTime.now());

        if (isServiceCall()) {
            // trusted service, it already knows who the actor is
            auditLog.setActor(auditLogRequest.getActor());
            auditLog.setActorRole(auditLogRequest.getActorRole());
            auditLog.setHotelId(auditLogRequest.getHotelId());
        } else {
            // a user sent it (hotel-service passes the token on), identity comes from the account, not from the body
            Auth currentUser = currentUserService.getCurrentUser();
            auditLog.setActor(currentUser.getUsername());
            auditLog.setActorRole(currentUser.getRole().name());
            auditLog.setHotelId(currentUser.getRole() == AuthRole.ADMIN
                    ? auditLogRequest.getHotelId()
                    : currentUser.getHotelId());
        }

        auditLog.setAction(auditLogRequest.getAction());
        auditLog.setEntityType(auditLogRequest.getEntityType());
        auditLog.setEntityId(auditLogRequest.getEntityId());
        auditLog.setDetails(auditLogRequest.getDetails());

        return auditLogRepository.save(auditLog);
    }

    private boolean isServiceCall() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("SCOPE_audit.write"));
    }

    @Override
    public AuditLogPage search(Integer hotelId, String entityType, String actor, int page, int size) {

        AuthRole role = currentUserService.getRole();

        // managers only see the logs of their own hotel
        if (role == AuthRole.MANAGER) {
            hotelId = currentUserService.getHotelId();
        } else if (role != AuthRole.ADMIN) {
            throw new AccessDeniedException("You are not allowed to read audit logs");
        }

        int safeSize = Math.min(Math.max(size, 1), 100);

        Page<AuditLog> result = auditLogRepository.search(
                hotelId,
                blankToNull(entityType),
                blankToNull(actor),
                PageRequest.of(Math.max(page, 0), safeSize));

        return new AuditLogPage(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Override
    public void record(String actor, String actorRole, int hotelId, String action, String entityType, String entityId, String details) {

        AuditLog auditLog = new AuditLog();
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLog.setActor(actor);
        auditLog.setActorRole(actorRole);
        auditLog.setHotelId(hotelId);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);

        auditLogRepository.save(auditLog);
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
