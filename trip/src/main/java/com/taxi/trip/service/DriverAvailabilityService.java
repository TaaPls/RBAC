package com.taxi.trip.service;

import com.taxi.trip.entity.dto.DriverResponseDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverAvailabilityService {

    private final UserServiceClient userServiceClient;
    private final DriverCacheService driverCacheService;

    public DriverAvailabilityService(UserServiceClient userServiceClient, DriverCacheService driverCacheService) {
        this.userServiceClient = userServiceClient;
        this.driverCacheService = driverCacheService;
    }

    @Scheduled(fixedDelay = 30000) // Каждые 30 секунд
    public void refreshAvailableDrivers() {
        try {
            List<DriverResponseDto> availableDrivers = userServiceClient.available();

            driverCacheService.updateAvailableDrivers(availableDrivers);
        } catch (Exception e) {
            System.err.println("Refresh drivers fail: "+ e.getMessage());
        }
    }


    public DriverResponseDto findAvailableDriver() {
        DriverResponseDto driver = driverCacheService.popRandomAvailableDriver();

        if (driver != null) {
            return driver;
        }

        refreshAvailableDrivers();

        driver = driverCacheService.popRandomAvailableDriver();
        if (driver != null) {
            return driver;
        }

        throw new RuntimeException("No available drivers at the moment");
    }
}