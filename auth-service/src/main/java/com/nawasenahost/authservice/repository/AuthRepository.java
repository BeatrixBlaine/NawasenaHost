package com.nawasenahost.authservice.repository;

import com.nawasenahost.authservice.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Integer> {
}
