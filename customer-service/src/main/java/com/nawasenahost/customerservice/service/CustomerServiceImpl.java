package com.nawasenahost.customerservice.service;

import com.nawasenahost.customerservice.dto.CustomerRequest;
import com.nawasenahost.customerservice.entity.Customer;
import com.nawasenahost.customerservice.exception.CustomerNotFoundException;
import com.nawasenahost.customerservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer findById(int id) {

        Optional<Customer> tempCustomer = customerRepository.findById(id);

        Customer theCustomer;
        if(tempCustomer.isPresent()) {
            theCustomer = tempCustomer.get();
        } else {
            throw new CustomerNotFoundException("Customer not found with Id: " + id);
        }

        return theCustomer;
    }

    @Override
    public Customer save(CustomerRequest customerRequest) {

        Customer tempCustomer = new Customer();
        tempCustomer.setFirstName(customerRequest.getFirstName());
        tempCustomer.setLastName(customerRequest.getLastName());
        tempCustomer.setEmail(customerRequest.getEmail());
        tempCustomer.setPhone(customerRequest.getPhone());

        return customerRepository.save(tempCustomer);
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        customerRepository.deleteById(id);
    }

    @Override
    public Customer update(int id, CustomerRequest customerRequest) {

        Customer tempCustomer = findById(id);
        tempCustomer.setFirstName(customerRequest.getFirstName());
        tempCustomer.setLastName(customerRequest.getLastName());
        tempCustomer.setEmail(customerRequest.getEmail());
        tempCustomer.setPhone(customerRequest.getPhone());

        return customerRepository.save(tempCustomer);
    }
}
