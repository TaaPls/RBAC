package com.taxi.trip.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RateRequestDto {
    public RateRequestDto(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return rate;
    }

    @NotNull(message = "Rate must not be null")
    @Min(1)
    @Max(5)
    int rate;
}
