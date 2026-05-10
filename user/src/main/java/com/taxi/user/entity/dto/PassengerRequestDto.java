package com.taxi.user.entity.dto;

import com.taxi.user.entity.Passenger;
import jakarta.validation.constraints.*;

/**
 * DTO for {@link Passenger}
 */
public class PassengerRequestDto {
    @NotNull(message = "Valid name required")
    @NotEmpty(message = "Valid name required")
    @NotBlank
    private final String name;
    @NotNull(message = "Valid email required")
    @Email(message = "Valid email required")
    @NotEmpty(message = "Valid email required")
    @NotBlank(message = "Valid email required")
    private final String email;
    @NotNull(message = "Valid phone required")
    @NotEmpty(message = "Valid phone required")
    @NotBlank(message = "Valid phone required")
    private final String phone;
    @NotNull(message = "Valid password required")
    @Size(message = "3-32", min = 3, max = 32)
    @NotEmpty(message = "Valid password required")
    @NotBlank(message = "Valid password required")
    private final String password;

    public PassengerRequestDto(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}