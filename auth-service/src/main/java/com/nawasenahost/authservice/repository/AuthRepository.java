package com.nawasenahost.authservice.repository;

import com.nawasenahost.authservice.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Integer> {

    Optional<Auth> findByUsername(String username);
    List<Auth> findAllByHotelId(int hotelId);

}
