package com.taxi.user.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String license_number;

    public Driver() {
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status = DriverStatus.AVAILABLE;
    @Column(nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();
    @Column(nullable = false)
    private String password;

    public Driver(String password, String license_number, String phone, String email, String name) {
        this.password = password;
        this.license_number = license_number;
        this.phone = phone;
        this.email = email;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DriverStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public String getPassword() {
        return password;
    }
}

