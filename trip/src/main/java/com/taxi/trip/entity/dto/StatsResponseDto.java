package com.taxi.trip.entity.dto;

import java.math.BigDecimal;
import java.util.List;

public class StatsResponseDto {
    private List<TripResponseDto> trips;
    private int count;
    private BigDecimal meanPrice;

    public StatsResponseDto(List<TripResponseDto> trips, int count, BigDecimal meanPrice) {
        this.trips = trips;
        this.count = count;
        this.meanPrice = meanPrice;
    }

    public List<TripResponseDto> getTrips() {
        return trips;
    }

    public int getCount() {
        return count;
    }

    public BigDecimal getMeanPrice() {
        return meanPrice;
    }
}
