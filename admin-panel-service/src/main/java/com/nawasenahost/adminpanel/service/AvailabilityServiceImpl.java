package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.AvailabilityBlock;
import com.nawasenahost.adminpanel.dto.OccupancyReport;
import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.time.LocalDate;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final GatewayClient gatewayClient;

    public AvailabilityServiceImpl(GatewayClient gatewayClient) {
        this.gatewayClient = gatewayClient;
    }

    @Override
    public List<RoomResponse> findAvailableRooms(SessionData sessionData,
                                                 LocalDate checkInDate,
                                                 LocalDate checkOutDate) {
        return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<RoomResponse>>() {},
                "/api/availabilities/hotel/{hotelId}/available-rooms?checkInDate={checkIn}&checkOutDate={checkOut}",
                sessionData.hotelId(), checkInDate, checkOutDate);
    }

    @Override
    public List<AvailabilityBlock> findBlocks(SessionData sessionData, LocalDate from, LocalDate to) {
        return gatewayClient.getList(sessionData, new ParameterizedTypeReference<List<AvailabilityBlock>>() {},
                "/api/availabilities/hotel/{hotelId}/blocks?from={from}&to={to}", sessionData.hotelId(), from, to);
    }

    @Override
    public OccupancyReport getOccupancy(SessionData sessionData, LocalDate from, LocalDate to) {
        return gatewayClient.get(sessionData, OccupancyReport.class,
                "/api/inventory/hotel/{hotelId}/occupancy?from={from}&to={to}", sessionData.hotelId(), from, to);
    }

    @Override
    public void block(SessionData sessionData, int roomId, LocalDate startDate, LocalDate endDate, String status) {
        gatewayClient.post(sessionData, null, String.class,
                "/api/availabilities/block?roomId={roomId}&startDate={start}&endDate={end}&status={status}",
                roomId, startDate, endDate, status);
    }

    @Override
    public void unblock(SessionData sessionData, int roomId, LocalDate startDate, LocalDate endDate) {
        gatewayClient.delete(sessionData,
                "/api/availabilities/unblock?roomId={roomId}&startDate={start}&endDate={end}",
                roomId, startDate, endDate);
    }
}
