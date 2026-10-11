package com.nawasenahost.clientservice.repository;

import com.nawasenahost.clientservice.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
