package com.taxi.trip.entity.dto;

import com.taxi.trip.entity.TripStatus;
import jakarta.validation.constraints.NotNull;

public class TripStatusRequestDto {

    @NotNull(message = "Status is required")
    private TripStatus status;

    public TripStatusRequestDto(TripStatus status) {
        this.status = status;
    }

    public TripStatus getStatus() {
        return status;
    }
}
