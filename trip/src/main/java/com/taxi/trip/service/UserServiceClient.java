package com.taxi.trip.service;

import com.taxi.trip.entity.dto.DriverResponseDto;
import com.taxi.trip.entity.dto.PassengerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {

    @GetMapping("/passengers/{id}")
    PassengerResponseDto getPassenger(@PathVariable("id") Long passengerId);

    @PutMapping("/drivers/{id}/status")
    DriverResponseDto updateDriverStatus(
            @PathVariable("id") Long driverId,
            @RequestBody Map<String, String> statusRequest
    );

    @GetMapping("/drivers/available")
    List<DriverResponseDto> available();
}
