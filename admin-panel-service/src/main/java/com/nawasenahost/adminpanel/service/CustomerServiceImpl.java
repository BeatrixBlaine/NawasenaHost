package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.CustomerForm;
import com.nawasenahost.adminpanel.dto.CustomerResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final GatewayClient gatewayClient;

    public CustomerServiceImpl(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @Override
    public List<CustomerResponse> findAll(SessionData sessionData, String query) {
        return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<CustomerResponse>>() {},
                "/api/customers/hotel/{hotelId}?q={q}", sessionData.hotelId(), query == null ? "" : query);
    }

    @Override
    public CustomerResponse findById(SessionData sessionData, int customerId) {
        return gatewayClient.get(sessionData, CustomerResponse.class, "/api/customers/{customerId}", customerId);
    }

    @Override
    public CustomerResponse update(SessionData sessionData, int customerId, CustomerForm customerForm) {

        customerForm.setHotelId(sessionData.hotelId());

        return gatewayClient.put(sessionData, customerForm, CustomerResponse.class,
                "/api/customers/{customerId}", customerId);
    }
}
