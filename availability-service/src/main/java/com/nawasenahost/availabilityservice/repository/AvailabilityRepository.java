package com.nawasenahost.availabilityservice.repository;

import com.nawasenahost.availabilityservice.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {
}
