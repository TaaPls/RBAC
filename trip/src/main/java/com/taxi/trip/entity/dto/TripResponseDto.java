package com.taxi.trip.entity.dto;

import com.taxi.trip.entity.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TripResponseDto {
    private Long id;
    private Long passengerId;
    private TripStatus status;
    private String origin;
    private String destination;
    private BigDecimal price;
    private LocalDateTime createdAt;

    public TripResponseDto(Long id, Long passengerId, TripStatus status, String origin, String destination, BigDecimal price, LocalDateTime createdAt) {
        this.id = id;
        this.passengerId = passengerId;
        this.status = status;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
        this.createdAt = createdAt;
    }

    public TripResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public TripStatus getStatus() {
        return status;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}