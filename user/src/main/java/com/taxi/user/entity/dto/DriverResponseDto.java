package com.taxi.user.entity.dto;

import com.taxi.user.entity.Driver;

import java.time.LocalDateTime;

/**
 * DTO for {@link Driver}
 */
public class DriverResponseDto {
    private final Long id;
    private final String name;
    private final String email;
    private final String phone;
    private final String license_number;
    private final LocalDateTime created_at;

    public DriverResponseDto(Long id, String name, String email, String phone, String license_number, LocalDateTime created_at) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.license_number = license_number;
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

    public String getLicense_number() {
        return license_number;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public Long getId() {
        return id;
    }
}