package com.taxi.user;

import com.taxi.user.entity.Driver;
import com.taxi.user.entity.DriverRepository;
import com.taxi.user.entity.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/drivers")
public class DriversController {

    private final DriverService driverService;

    public DriversController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverResponseDto> register(@Valid @RequestBody DriverRequestDto request) {
        DriverResponseDto response = driverService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = driverService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == principal.id")
    public ResponseEntity<DriverResponseDto> getProfile(@PathVariable Long id) {
        DriverResponseDto response = driverService.getProfile(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SERVICE') or #id == principal.id")
    public ResponseEntity<DriverResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody DriverStatusRequestDto request) {
        DriverResponseDto response = driverService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DriverResponseDto>> getAvailable() {
        List<DriverResponseDto> availableDrivers = driverService.getAvailableDrivers();
        return ResponseEntity.ok(availableDrivers);
    }
}
