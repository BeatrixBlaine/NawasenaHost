package com.nawasenahost.userservice.service;

import com.nawasenahost.userservice.dto.UserRequest;
import com.nawasenahost.userservice.entity.User;
import com.nawasenahost.userservice.exception.UserNotFoundException;
import com.nawasenahost.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id) {

        Optional<User> tempUser = userRepository.findById(id);

        User theUser;
        if(tempUser.isPresent()) {
            theUser = tempUser.get();
        } else {
            throw new UserNotFoundException("User not found with Id: " + id);
        }

        return theUser;
    }

    @Override
    public User save(UserRequest userRequest) {

        User tempUser = new User();
        tempUser.setFirstName(userRequest.getFirstName());
        tempUser.setLastName(userRequest.getLastName());
        tempUser.setEmail(userRequest.getEmail());
        tempUser.setPhone(userRequest.getPhone());

        return userRepository.save(tempUser);
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        userRepository.deleteById(id);
    }

    @Override
    public User update(int id, UserRequest userRequest) {

        User tempUser = findById(id);
        tempUser.setFirstName(userRequest.getFirstName());
        tempUser.setLastName(userRequest.getLastName());
        tempUser.setEmail(userRequest.getEmail());
        tempUser.setPhone(userRequest.getPhone());

        return userRepository.save(tempUser);
    }
}
