package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.SessionExpiredException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {

    private static final String SESSION_KEY = "sessionData";

    @Override
    public void save(HttpSession session, SessionData sessionData) {
        session.setAttribute(SESSION_KEY, sessionData);
    }

    @Override
    public SessionData get(HttpSession session) {

        SessionData sessionData = (SessionData) session.getAttribute(SESSION_KEY);

        if (sessionData == null) {
            throw new SessionExpiredException("Session expired, please log in again.");
        }

        return sessionData;
    }
}
