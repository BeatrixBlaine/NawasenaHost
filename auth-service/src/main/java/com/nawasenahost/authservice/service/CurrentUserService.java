package com.nawasenahost.authservice.service;

import com.nawasenahost.authservice.entity.Auth;
import com.nawasenahost.authservice.entity.AuthRole;

public interface CurrentUserService {

    Auth getCurrentUser();
    AuthRole getRole();
    int getHotelId();

}
