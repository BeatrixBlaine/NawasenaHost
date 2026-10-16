package com.nawasenahost.authservice.dto;

public class LoginResponse {

    private int authId;
    private String role;
    private int hotelId;
    private String token;
    private String tokenType;

    public LoginResponse(){};

    public LoginResponse(int authId, String role, int hotelId, String token, String tokenType) {
        this.authId = authId;
        this.role = role;
        this.hotelId = hotelId;
        this.token = token;
        this.tokenType = tokenType;
    }

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
