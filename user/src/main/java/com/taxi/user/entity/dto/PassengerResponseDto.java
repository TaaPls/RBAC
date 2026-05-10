package com.taxi.user.entity.dto;

import java.time.LocalDateTime;

public class PassengerResponseDto {
    private final String name;
    private final String email;
    private final String phone;
    private final LocalDateTime created_at;

    public PassengerResponseDto(String name, String email, String phone, LocalDateTime created_at) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
}
