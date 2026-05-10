package com.taxi.user.entity.dto;

import com.taxi.user.entity.DriverStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class DriverStatusRequestDto {
    private final DriverStatus driverStatus;

    public DriverStatusRequestDto(DriverStatus driverStatus) {
        this.driverStatus = driverStatus;
    }

    public DriverStatus getDriverStatus() {
        return driverStatus;
    }
}
