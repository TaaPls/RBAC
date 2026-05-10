package com.taxi.trip;

import com.taxi.trip.entity.dto.*;
import com.taxi.trip.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    ResponseEntity<TripResponseDto> postTrip(@Valid @RequestBody TripRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponseDto> getTrip(@PathVariable Long id) {
        TripResponseDto response = tripService.getTrip(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('PASSENGER') and #passengerId == principal.id")
    @GetMapping
    public ResponseEntity<List<TripResponseDto>> getPassengerTrips(
            @RequestParam("passenger_id") Long passengerId) {
        List<TripResponseDto> trips = tripService.getPassengerTrips(passengerId);
        return ResponseEntity.ok(trips);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TripResponseDto> updateTripStatus(
            @PathVariable Long id,
            @Valid @RequestBody TripStatusRequestDto request) {
        TripResponseDto response = tripService.updateTripStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponseDto> getStats(
            @RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(tripService.getStats(date));
    }

    @PreAuthorize("hasRole('PASSENGER')")
    @PutMapping("/{id}/rate")
    public ResponseEntity<TripResponseDto> rate(
            @Valid @RequestBody RateRequestDto request,
            @PathVariable Long id
            ) {
        return ResponseEntity.ok(tripService.rateTrip(id, request));
    }
}

