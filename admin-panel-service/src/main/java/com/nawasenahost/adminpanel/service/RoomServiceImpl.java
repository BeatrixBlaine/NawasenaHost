package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.RoomForm;
import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    private final GatewayClient gatewayClient;

    public RoomServiceImpl(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @Override
    public List<RoomResponse> findAll(SessionData sessionData) {
        return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<RoomResponse>>() {},
                "/api/rooms/{hotelId}", sessionData.hotelId());
    }

    @Override
    public RoomResponse findById(SessionData sessionData, int roomId) {
        return gatewayClient.get(sessionData, RoomResponse.class, "/api/rooms/single-room/{roomId}", roomId);
    }

    @Override
    public RoomResponse save(SessionData sessionData, RoomForm roomForm) {

        // the hotel always comes from the session of the user, never from the form
        roomForm.setHotelId(sessionData.hotelId());

        return gatewayClient.post(sessionData, roomForm, RoomResponse.class, "/api/rooms");
    }

    @Override
    public RoomResponse update(SessionData sessionData, int roomId, RoomForm roomForm) {

        roomForm.setHotelId(sessionData.hotelId());

        return gatewayClient.put(sessionData, roomForm, RoomResponse.class, "/api/rooms/{roomId}", roomId);
    }

    @Override
    public void delete(SessionData sessionData, int roomId) {
        gatewayClient.delete(sessionData, "/api/rooms/{roomId}", roomId);
    }
}
