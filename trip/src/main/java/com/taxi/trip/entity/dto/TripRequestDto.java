package com.taxi.trip.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TripRequestDto {

    @NotNull(message = "Passenger ID is required")
    private Long passengerId;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    public TripRequestDto(Long passengerId, String origin, String destination) {
        this.passengerId = passengerId;
        this.origin = origin;
        this.destination = destination;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }
}