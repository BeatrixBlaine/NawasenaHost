package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.SessionData;

public interface AuthService {

    SessionData login(String username, String password);

}
