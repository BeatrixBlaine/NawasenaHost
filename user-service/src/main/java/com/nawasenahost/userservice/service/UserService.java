package com.nawasenahost.userservice.service;

import com.nawasenahost.userservice.dto.UserRequest;
import com.nawasenahost.userservice.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();
    User findById(int id);
    User save(UserRequest userRequest);
    void deleteById(int id);
    User update(int id, UserRequest userRequest);

}
