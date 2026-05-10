package com.taxi.user;

import com.taxi.user.entity.Passenger;
import com.taxi.user.entity.PassengerRepository;
import com.taxi.user.entity.dto.LoginRequestDto;
import com.taxi.user.entity.dto.LoginResponseDto;
import com.taxi.user.entity.dto.PassengerRequestDto;
import com.taxi.user.entity.dto.PassengerResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/passengers")
public class PassengersController {

    private final PassengerService passengerService;

    public PassengersController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == principal.id or hasRole('SERVICE')")
    public ResponseEntity<PassengerResponseDto> getProfile(@PathVariable Long id) {
        PassengerResponseDto response = passengerService.getProfile(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PassengerResponseDto> create(@Valid @RequestBody PassengerRequestDto passenger) {
        PassengerResponseDto passengerResponseDto = passengerService.register(passenger);
        return ResponseEntity.status(HttpStatus.CREATED).body(passengerResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = passengerService.login(request);
        return ResponseEntity.ok(response);
    }
}
