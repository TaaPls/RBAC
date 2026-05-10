package com.taxi.user;

import com.taxi.user.entity.Driver;
import com.taxi.user.entity.DriverRepository;
import com.taxi.user.entity.DriverStatus;
import com.taxi.user.entity.dto.*;
import com.taxi.user.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public DriverService(DriverRepository driverRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public DriverResponseDto register(DriverRequestDto request) {
        if (driverRepository.findByEmail(request.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        Driver driver = new Driver(
                passwordEncoder.encode(request.getPassword()),
                                request.getLicense_number(),
                                request.getPhone(),
                                request.getEmail(),
                                request.getName());

        Driver savedDriver = driverRepository.save(driver);
        return mapToResponse(savedDriver);
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Driver driver = driverRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        String token = jwtUtil.generateToken(
                driver.getEmail(),
                "DRIVER",
                driver.getId()
        );

        return new LoginResponseDto(token);
    }

    @Transactional(readOnly = true)
    public DriverResponseDto getProfile(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
        return mapToResponse(driver);
    }

    @Transactional(readOnly = true)
    public List<DriverResponseDto> getAvailableDrivers() {
        List<Driver> drivers = driverRepository.findDriversByStatus(DriverStatus.AVAILABLE);
        return drivers.stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public DriverResponseDto updateStatus(Long id, DriverStatusRequestDto request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        driver.setStatus(request.getDriverStatus());
        Driver updatedDriver = driverRepository.save(driver);
        return mapToResponse(updatedDriver);
    }

    private DriverResponseDto mapToResponse(Driver driver) {
        return new DriverResponseDto(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getPhone(),
                driver.getLicense_number(),
                driver.getCreated_at()
        );
    }
}