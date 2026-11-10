package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final WebClient webClient;

    public RoomServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<RoomResponse> findAll(SessionData sessionData) {

        try {
            return webClient.get()
                    .uri("/api/rooms/{hotelId}", sessionData.hotelId())
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<RoomResponse>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }
}
