package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.dto.UserForm;
import com.nawasenahost.adminpanel.dto.UserResponse;
import java.util.List;

public interface UserService {

    List<UserResponse> findAll(SessionData sessionData);
    UserResponse findById(SessionData sessionData, int userId);
    UserResponse save(SessionData sessionData, UserForm userForm);
    UserResponse update(SessionData sessionData, int userId, UserForm userForm);

}
