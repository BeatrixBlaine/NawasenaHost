package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.CustomerForm;
import com.nawasenahost.adminpanel.dto.CustomerResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.util.List;

public interface CustomerService {

    List<CustomerResponse> findAll(SessionData sessionData, String query);
    CustomerResponse findById(SessionData sessionData, int customerId);
    CustomerResponse update(SessionData sessionData, int customerId, CustomerForm customerForm);

}
