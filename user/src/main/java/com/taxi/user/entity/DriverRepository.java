package com.taxi.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    @Override
    Optional<Driver> findById(Long aLong);

    Optional<Driver> findByEmail(String email);

    List<Driver> findDriversByStatus(DriverStatus status);
}