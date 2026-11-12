package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.AuditLogPageResponse;
import com.nawasenahost.adminpanel.dto.SessionData;

public interface AuditLogService {

    AuditLogPageResponse search(SessionData sessionData, Integer hotelId, String entityType, String actor, int page);

}
