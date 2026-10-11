package com.nawasenahost.clientservice.service;

import com.nawasenahost.clientservice.dto.ClientRequest;
import com.nawasenahost.clientservice.entity.Client;

import java.util.List;

public interface ClientService {

    List<Client> findAll();
    Client findById(int id);
    Client save(ClientRequest clientRequest);
    void deleteById(int id);
    Client update(int id, ClientRequest clientRequest);

}
