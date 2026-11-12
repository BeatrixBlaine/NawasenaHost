package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.SessionData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

// every call to the backend goes through here, so the token and the error handling are written once
@Component
public class GatewayClient {

    private final WebClient webClient;

    public GatewayClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> T get(SessionData sessionData, Class<T> type, String uri, Object... variables) {

        try {
            return webClient.get()
                    .uri(uri, variables)
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(type)
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }

    public <T> List<T> getList(SessionData sessionData, ParameterizedTypeReference<List<T>> type, String uri, Object... variables) {

        try {
            return webClient.get()
                    .uri(uri, variables)
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .bodyToMono(type)
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }

    public <T> T post(SessionData sessionData, Object body, Class<T> type, String uri, Object... variables) {

        try {
            WebClient.RequestBodySpec request = webClient.post()
                    .uri(uri, variables)
                    .header("Authorization", ApiSupport.bearer(sessionData));

            WebClient.RequestHeadersSpec<?> spec = body == null ? request : request.bodyValue(body);

            return spec.retrieve().bodyToMono(type).block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }

    public <T> T put(SessionData sessionData, Object body, Class<T> type, String uri, Object... variables) {

        try {
            WebClient.RequestBodySpec request = webClient.put()
                    .uri(uri, variables)
                    .header("Authorization", ApiSupport.bearer(sessionData));

            WebClient.RequestHeadersSpec<?> spec = body == null ? request : request.bodyValue(body);

            return spec.retrieve().bodyToMono(type).block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }

    public void delete(SessionData sessionData, String uri, Object... variables) {

        try {
            webClient.delete()
                    .uri(uri, variables)
                    .header("Authorization", ApiSupport.bearer(sessionData))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw ApiSupport.toApiException(e);
        }
    }
}
