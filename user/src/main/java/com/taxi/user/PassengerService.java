package com.taxi.user;

import com.taxi.user.entity.Passenger;
import com.taxi.user.entity.PassengerRepository;
import com.taxi.user.entity.dto.LoginRequestDto;
import com.taxi.user.entity.dto.LoginResponseDto;
import com.taxi.user.entity.dto.PassengerRequestDto;
import com.taxi.user.entity.dto.PassengerResponseDto;
import com.taxi.user.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public PassengerService(PassengerRepository passengerRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.passengerRepository = passengerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public PassengerResponseDto register(PassengerRequestDto request) {
        if (passengerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        Passenger passenger = new Passenger(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()));

        Passenger savedPassenger = passengerRepository.save(passenger);
        return mapToResponse(savedPassenger);
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Passenger passenger = passengerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found"));

        String token = jwtUtil.generateToken(
                passenger.getEmail(),
                "PASSENGER",
                passenger.getId()
        );

        return new LoginResponseDto(token);
    }

    public PassengerResponseDto getProfile(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found"));
        return mapToResponse(passenger);
    }

    private PassengerResponseDto mapToResponse(Passenger passenger) {
        return new PassengerResponseDto(
                passenger.getName(),
                passenger.getEmail(),
                passenger.getPhone(),
                passenger.getCreated_at());
    }
}