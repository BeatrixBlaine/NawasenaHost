package com.nawasenahost.clientservice.rest;

import com.nawasenahost.clientservice.dto.ClientRequest;
import com.nawasenahost.clientservice.entity.Client;
import com.nawasenahost.clientservice.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientRestController {

    private final ClientService clientService;

    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/users")
    public List<Client> getUsers() {
        return clientService.findAll();
    }

    @GetMapping("/users/{userId}")
    public Client getUser(@PathVariable int userId) {
        return clientService.findById(userId);
    }

    @PostMapping("/users")
    public Client registerUser(@Valid @RequestBody ClientRequest clientRequest) {
        return clientService.save(clientRequest);
    }

    @PutMapping("/users/{userId}")
    public Client updateUser(@PathVariable int userId,
                             @Valid @RequestBody ClientRequest clientRequest) {
        return clientService.update(userId, clientRequest);
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable int userId) {
        clientService.deleteById(userId);
        return "User with Id: " + userId + " deleted";
    }
}
