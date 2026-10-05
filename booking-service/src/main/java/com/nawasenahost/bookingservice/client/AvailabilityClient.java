package com.nawasenahost.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "availability-service")
public interface AvailabilityClient {

    @GetMapping("/api/availabilities/{availabilityId}")
    ResponseEntity<Void> getAvailability(@PathVariable int availabilityId);

}
