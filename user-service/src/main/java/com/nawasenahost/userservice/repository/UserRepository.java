package com.nawasenahost.userservice.repository;

import com.nawasenahost.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
