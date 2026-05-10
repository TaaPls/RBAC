package com.taxi.trip.entity.dto;

import java.time.LocalDateTime;


public class DriverResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String license_number;
    private LocalDateTime created_at;

    public DriverResponseDto() {
    }

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLicense_number(String license_number) {
        this.license_number = license_number;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}