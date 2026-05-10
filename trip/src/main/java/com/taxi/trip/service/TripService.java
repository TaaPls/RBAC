package com.taxi.trip.service;

import com.taxi.trip.entity.Trip;
import com.taxi.trip.entity.TripRepository;
import com.taxi.trip.entity.TripStatus;
import com.taxi.trip.entity.dto.*;
import com.taxi.trip.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final UserServiceClient userServiceClient;
    private final DriverAvailabilityService driverAvailabilityService;
    private final DriverCacheService driverCacheService;
    private final NotificationServiceClient notificationServiceClient;
    private final float tariff = 1.5f;

    public TripService(TripRepository tripRepository, UserServiceClient userServiceClient, DriverAvailabilityService driverAvailabilityService, DriverCacheService driverCacheService, NotificationServiceClient notificationServiceClient) {
        this.tripRepository = tripRepository;
        this.userServiceClient = userServiceClient;
        this.driverAvailabilityService = driverAvailabilityService;
        this.driverCacheService = driverCacheService;
        this.notificationServiceClient = notificationServiceClient;
    }

    @Transactional
    public TripResponseDto createTrip(TripRequestDto request) {
        Optional<Trip> exists = tripRepository.getTripByPassengerIdAndStatusNot(request.getPassengerId(), TripStatus.COMPLETED);
        if (exists.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Trip from passenger " + request.getPassengerId() + " already exists");
        }

        BigDecimal price = getPrice();
        Trip trip = new Trip(
                request.getPassengerId(),
                request.getOrigin(),
                request.getDestination(),
                price
        );

        trip = tripRepository.save(trip);
        try {
            notificationServiceClient.createNotification(new NotificationRequestDto(
                    trip.getId(),
                    "PASSENGER",
                    trip.getPassengerId(),
                    "Trip '" + trip.getOrigin() + "' to '" + trip.getDestination() + "' scheduled for passenger "
                            + trip.getPassengerId() + " for " + trip.getPrice() + " ID: " + trip.getId()
            ));
        } catch (Exception e) {
            System.err.println("Notification service not available right now");
        }
        try {
            assignDriver(trip);
        } catch (Exception e) {
            System.err.println("Could not immediately assign driver for trip: "+trip.getId());
        }

        return mapToResponse(trip);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void assignDriver(Trip trip) {
        DriverResponseDto driver = driverAvailabilityService.findAvailableDriver();

        if (driver == null) {
            System.err.println("No available drivers");
            return;
        }

        updateDriverStatus(driver.getId(), "NOT_AVAILABLE");

        trip.setDriverId(driver.getId());
        trip.setStatus(TripStatus.ACCEPTED);
        tripRepository.save(trip);

        try {
            notificationServiceClient.createNotification(new NotificationRequestDto(
                    trip.getId(),
                    "PASSENGER",
                    trip.getPassengerId(),
                    "Driver " + driver.getName() + " assigned to trip '" + trip.getOrigin() + "' to '" + trip.getDestination() + " ID " + trip.getId()
            ));

            notificationServiceClient.createNotification(new NotificationRequestDto(
                    trip.getId(),
                    "DRIVER",
                    driver.getId(),
                    "You assigned to trip '" + trip.getOrigin() + "' to '" + trip.getDestination() + " ID " + trip.getId()
            ));
        } catch (Exception e) {
            System.err.println("Notification service not available right now");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TripResponseDto updateTripStatus(Long tripId, TripStatusRequestDto request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found: " + tripId));

        Long driverId;
        if (Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal() instanceof CustomUserDetails o) {
            driverId = o.getId();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No authorization");
        };

        if (request.getStatus() != TripStatus.CREATED
                && trip.getDriverId() != null && !Objects.equals(driverId, trip.getDriverId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not trip driver");
        }

        TripStatus newStatus = request.getStatus();
        trip.setStatus(newStatus);

        switch (newStatus) {
            case CREATED:
                if (trip.getDriverId() == null) {
                    try{
                        assignDriver(trip);
                    } catch (Exception e) {
                        System.err.println("Could not assign driver for trip: "+trip.getId());
                    }
                }
                break;
            case COMPLETED:
                if (trip.getDriverId() != null) {
                    DriverResponseDto driver = updateDriverStatus(trip.getDriverId(), "AVAILABLE");
                    driverCacheService.returnDriverToAvailable(driver);

                    try {
                        notificationServiceClient.createNotification(new NotificationRequestDto(
                                trip.getId(),
                                "PASSENGER",
                                trip.getPassengerId(),
                                "Trip '" + trip.getOrigin() + "' to '" + trip.getDestination() + "' ID " + trip.getId()
                                + " completed"
                        ));
                    } catch (Exception e) {
                        System.err.println("Notification service not available right now");
                    }
                }
                break;
            default:
                break;
        }

        trip = tripRepository.save(trip);
        return mapToResponse(trip);
    }

    @Transactional(readOnly = true)
    public TripResponseDto getTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found: " + id));
        return mapToResponse(trip);
    }

    @Transactional(readOnly = true)
    public List<TripResponseDto> getPassengerTrips(Long passengerId) {
        return tripRepository.findTripsByPassengerIdOrderByCreatedAtDesc(passengerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DriverResponseDto updateDriverStatus(Long driverId, String status) {
        if (driverId == null) return null;

        try {
            Map<String, String> statusRequest = new HashMap<>();
            statusRequest.put("driverStatus", status);
            return userServiceClient.updateDriverStatus(driverId, statusRequest);
        } catch (Exception e) {
            System.err.println("Failed to update driver status: "+ driverId + " " + e.getMessage());
            return null;
        }
    }

    private TripResponseDto mapToResponse(Trip trip) {
        return new TripResponseDto(
                trip.getId(),
                trip.getPassengerId(),
                trip.getStatus(),
                trip.getOrigin(),
                trip.getDestination(),
                trip.getPrice(),
                trip.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public StatsResponseDto getStats(LocalDate date) {
        List<Trip> trips = tripRepository.findTripsByCreatedAtBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        BigDecimal mean = BigDecimal.valueOf(0.0);
        int count = trips.size();
        for (var trip : trips) mean = mean.add(trip.getPrice());
        return new StatsResponseDto(trips.stream().map(this::mapToResponse).toList(), count, mean);
    }

    @Transactional
    public TripResponseDto rateTrip(Long id, RateRequestDto request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found: " + id));

        Long passengerId;
        if (Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal() instanceof CustomUserDetails o) {
            passengerId = o.getId();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No authorization");
        };
        if (!Objects.equals(passengerId, trip.getPassengerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not trip passenger");
        }
        
        trip.setRate(request.getRate());
        tripRepository.save(trip);
        return mapToResponse(trip);
    }

    BigDecimal getPrice() {
        BigDecimal min = new BigDecimal("10.00");
        BigDecimal max = new BigDecimal("100.00");
        BigDecimal range = max.subtract(min);
        BigDecimal randomBD = min.add(range.multiply(new BigDecimal(Math.random())));

        randomBD = randomBD.setScale(2, RoundingMode.HALF_UP);
        return randomBD.multiply(new BigDecimal(tariff));
    }
}