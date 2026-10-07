package com.nawasenahost.userservice.rest;

import com.nawasenahost.userservice.dto.UserRequest;
import com.nawasenahost.userservice.entity.User;
import com.nawasenahost.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable int userId) {
        return userService.findById(userId);
    }

    @PostMapping("/users")
    public User registerUser(@Valid @RequestBody UserRequest userRequest) {
        return userService.save(userRequest);
    }

    @PutMapping("/users/{userId}")
    public User updateUser(@PathVariable int userId,
                           @Valid @RequestBody UserRequest userRequest) {
        return userService.update(userId, userRequest);
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable int userId) {
        userService.deleteById(userId);
        return "User with Id: " + userId + " deleted";
    }
}
