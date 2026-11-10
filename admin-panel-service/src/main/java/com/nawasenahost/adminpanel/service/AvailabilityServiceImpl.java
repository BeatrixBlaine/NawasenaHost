package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final WebClient webClient;

    public AvailabilityServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<RoomResponse> findAvailableRooms(SessionData sessionData,
                                                 LocalDate checkInDate,
                                                 LocalDate checkOutDate) {

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/availabilities/hotel/{hotelId}/available-rooms")
                            .queryParam("checkInDate", checkInDate)
                            .queryParam("checkOutDate", checkOutDate)
                            .build(sessionData.hotelId()))
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<RoomResponse>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }
}
