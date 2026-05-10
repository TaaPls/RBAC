package com.taxi.trip.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findTripsByPassengerIdOrderByCreatedAtDesc(Long passengerId);

    @Override
    Optional<Trip> findById(Long id);

    List<Trip> findTripsByCreatedAtBetween(LocalDateTime createdAtStart, LocalDateTime createdAtEnd);

    Optional<Trip> getTripByPassengerIdAndStatusNot(Long passengerId, TripStatus status);
}