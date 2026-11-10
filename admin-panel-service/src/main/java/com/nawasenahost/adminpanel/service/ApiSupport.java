package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.SessionData;
import com.nawasenahost.adminpanel.exception.ApiException;
import com.nawasenahost.adminpanel.exception.ErrorResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;

// shared helpers for the services that talk to the backend through api-gateway
public final class ApiSupport {

    private ApiSupport() {
    }

    public static String bearer(SessionData sessionData) {
        return sessionData.tokenType() + " " + sessionData.token();
    }

    // turns the error response of a backend service into our own ErrorResponse
    public static ApiException toApiException(WebClientResponseException e) {

        ErrorResponse error;

        try {
            error = e.getResponseBodyAs(ErrorResponse.class);
        } catch (Exception ignored) {
            error = null;
        }

        if (error == null || error.getMessage() == null) {
            error = new ErrorResponse(
                    e.getStatusCode().value(),
                    "An unexpected error occurred.",
                    System.currentTimeMillis()
            );
        }

        error.setStatus(e.getStatusCode().value());

        return new ApiException(error);
    }
}
