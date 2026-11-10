package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.SessionData;
import jakarta.servlet.http.HttpSession;

public interface SessionService {

    void save(HttpSession session, SessionData sessionData);
    SessionData get(HttpSession session);

}
