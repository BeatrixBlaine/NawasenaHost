package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.dto.UserForm;
import com.nawasenahost.adminpanel.dto.UserResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final GatewayClient gatewayClient;

    public UserServiceImpl(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    // an ADMIN sees the users of every hotel, a MANAGER only the users of the hotel of the account
    @Override
    public List<UserResponse> findAll(SessionData sessionData) {
        if ("ADMIN".equals(sessionData.role())) {
            return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<UserResponse>>() {},
                    "/api/auth/users");
        }

        return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<UserResponse>>() {},
                "/api/auth/users?hotelId={hotelId}", sessionData.hotelId());
    }

    @Override
    public UserResponse findById(SessionData sessionData, int userId) {
        return gatewayClient.get(sessionData, UserResponse.class, "/api/auth/users/{userId}", userId);
    }

    @Override
    public UserResponse save(SessionData sessionData, UserForm userForm) {

        // a MANAGER can only add STAFF to the own hotel, auth-service checks this again
        if (!"ADMIN".equals(sessionData.role())) {
            userForm.setRole("STAFF");
            userForm.setHotelId(sessionData.hotelId());
        }

        return gatewayClient.post(sessionData, userForm, UserResponse.class, "/api/auth/users");
    }

    @Override
    public UserResponse update(SessionData sessionData, int userId, UserForm userForm) {
        return gatewayClient.put(sessionData, userForm, UserResponse.class, "/api/auth/users/{userId}", userId);
    }
}
