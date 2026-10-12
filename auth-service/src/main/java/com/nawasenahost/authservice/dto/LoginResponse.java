package com.nawasenahost.authservice.dto;

public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private String username;
    private String role;

    public LoginResponse(String accessToken, String tokenType, String username, String role) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.username = username;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
