package com.taxi.user.entity.dto;

public class LoginResponseDto {
    public LoginResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    private final String token;
}
