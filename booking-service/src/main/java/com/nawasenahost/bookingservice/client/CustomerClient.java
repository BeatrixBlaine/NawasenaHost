package com.nawasenahost.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface CustomerClient {

    @GetMapping("/api/users/{customerId}")
    ResponseEntity<Void> getCustomer(@PathVariable int customerId);

}
