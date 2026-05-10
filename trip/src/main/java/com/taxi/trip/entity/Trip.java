package com.taxi.trip.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips", indexes = {
        @Index(name = "idx_trips_status", columnList = "status")
})
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long passengerId;

    private Long driverId = null;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TripStatus status = TripStatus.CREATED;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    private int rate = 0;

    public Trip() {
    }

    public Trip(Long passenger_id, String origin, String destination, BigDecimal price) {
        this.passengerId = passenger_id;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public Long getDriverId() {
        return driverId;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getRate() {
        return rate;
    }

    public void setDriverId(Long driver_id) {
        this.driverId = driver_id;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}

