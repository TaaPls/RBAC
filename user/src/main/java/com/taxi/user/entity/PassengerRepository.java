package com.taxi.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    Optional<Passenger> findByEmail(String email);

    @Override
    Optional<Passenger> findById(Long aLong);
}