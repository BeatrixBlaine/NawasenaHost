package com.nawasenahost.adminpanel.dto;

public class LoginResponse {

    private int authId;
    private int hotelId;
    private String token;
    private String tokenType;

    public LoginResponse(){};

    public LoginResponse(int authId, int hotelId, String token, String tokenType) {
        this.authId = authId;
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
