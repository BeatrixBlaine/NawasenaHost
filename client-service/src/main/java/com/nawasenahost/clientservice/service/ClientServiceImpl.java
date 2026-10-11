package com.nawasenahost.clientservice.service;

import com.nawasenahost.clientservice.dto.ClientRequest;
import com.nawasenahost.clientservice.entity.Client;
import com.nawasenahost.clientservice.exception.ClientNotFoundException;
import com.nawasenahost.clientservice.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client findById(int id) {

        Optional<Client> tempUser = clientRepository.findById(id);

        Client theClient;
        if(tempUser.isPresent()) {
            theClient = tempUser.get();
        } else {
            throw new ClientNotFoundException("User not found with Id: " + id);
        }

        return theClient;
    }

    @Override
    public Client save(ClientRequest clientRequest) {

        Client tempClient = new Client();
        tempClient.setFirstName(clientRequest.getFirstName());
        tempClient.setLastName(clientRequest.getLastName());
        tempClient.setEmail(clientRequest.getEmail());
        tempClient.setPhone(clientRequest.getPhone());

        return clientRepository.save(tempClient);
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        clientRepository.deleteById(id);
    }

    @Override
    public Client update(int id, ClientRequest clientRequest) {

        Client tempClient = findById(id);
        tempClient.setFirstName(clientRequest.getFirstName());
        tempClient.setLastName(clientRequest.getLastName());
        tempClient.setEmail(clientRequest.getEmail());
        tempClient.setPhone(clientRequest.getPhone());

        return clientRepository.save(tempClient);
    }
}
