package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.HotelForm;
import com.nawasenahost.adminpanel.dto.HotelResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl implements HotelService {

    private final GatewayClient gatewayClient;

    public HotelServiceImpl(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @Override
    public List<HotelResponse> findAll(SessionData sessionData) {
        return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<HotelResponse>>() {},
                "/api/hotels");
    }

    @Override
    public HotelResponse findById(SessionData sessionData, int hotelId) {
        return gatewayClient.get(sessionData, HotelResponse.class, "/api/hotels/{hotelId}", hotelId);
    }

    @Override
    public HotelResponse save(SessionData sessionData, HotelForm hotelForm) {
        return gatewayClient.post(sessionData, hotelForm, HotelResponse.class, "/api/hotels");
    }

    @Override
    public HotelResponse update(SessionData sessionData, int hotelId, HotelForm hotelForm) {
        return gatewayClient.put(sessionData, hotelForm, HotelResponse.class, "/api/hotels/{hotelId}", hotelId);
    }

    @Override
    public void delete(SessionData sessionData, int hotelId) {
        gatewayClient.delete(sessionData, "/api/hotels/{hotelId}", hotelId);
    }
}
