package com.nawasenahost.adminpanel.controller;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.service.AuditLogService;
import com.nawasenahost.adminpanel.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuditLogController {

    private final SessionService sessionService;
    private final AuditLogService auditLogService;

    public AuditLogController(SessionService sessionService,
                              AuditLogService auditLogService) {
        this.sessionService = sessionService;
        this.auditLogService = auditLogService;
    }

    // ADMIN can look at one hotel or at every hotel, MANAGER always sees the own hotel
    @GetMapping("/audit-logs")
    public String auditLogs(@RequestParam(required = false) Integer hotelId,
                            @RequestParam(required = false) String entityType,
                            @RequestParam(required = false) String actor,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "current") String scope,
                            HttpSession session,
                            Model model) {

        SessionData sessionData = sessionService.get(session);

        boolean admin = "ADMIN".equals(sessionData.role());

        // ADMIN: "all" removes the hotel filter, otherwise the active hotel is used
        Integer filterHotelId = admin && "all".equals(scope) ? hotelId : sessionData.hotelId();

        model.addAttribute("activePage", "audit");
        model.addAttribute("entityType", entityType);
        model.addAttribute("actor", actor);
        model.addAttribute("scope", scope);

        try {
            model.addAttribute("logs", auditLogService.search(sessionData, filterHotelId, entityType, actor, Math.max(page, 0)));
        } catch (ApiException e) {
            model.addAttribute("error", e.getError().getMessage());
        }

        return "audit-logs";
    }
}
