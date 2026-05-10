package com.taxi.user.security;

import com.taxi.user.entity.DriverRepository;
import com.taxi.user.entity.PassengerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;

    public CustomUserDetailsService(PassengerRepository passengerRepository, DriverRepository driverRepository) {
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return passengerRepository.findByEmail(email)
                .map(CustomUserDetails::create)
                .orElseGet(() -> driverRepository.findByEmail(email)
                        .map(CustomUserDetails::create)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "User not found with email: " + email)));
    }
}