package com.taxi.user.entity.dto;

import com.taxi.user.entity.Driver;
import jakarta.validation.constraints.*;

/**
 * DTO for {@link Driver}
 */
public class DriverRequestDto {
    @NotNull(message = "Name can not be null")
    @Size(message = "Name size: 3-255", min = 3, max = 255)
    @Pattern(regexp = "^[A-Za-z ]+$")
    @NotEmpty(message = "Name can not be empty")
    @NotBlank(message = "Name can not be blank")
    private final String name;
    @NotNull(message = "Email can not be null")
    @Email(message = "Not a valid email")
    @NotEmpty(message = "Email can not be empty")
    @NotBlank(message = "Email can not be blank")
    private final String email;
    @NotNull(message = "Valid phone required")
    @NotEmpty(message = "Valid phone required")
    @NotBlank(message = "Valid phone required")
    private final String phone;
    @NotNull(message = "Valid lp required")
    @NotEmpty(message = "Valid lp required")
    @NotBlank(message = "Valid lp required")
    private final String license_number;
    @NotNull(message = "Valid password required")
    @Size(message = "Valid password required", min = 3, max = 32)
    @NotEmpty(message = "Valid password required")
    @NotBlank(message = "Valid password required")
    private final String password;

    public DriverRequestDto(String name, String email, String phone, String license_number, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.license_number = license_number;
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

    public String getLicense_number() {
        return license_number;
    }

    public String getPassword() {
        return password;
    }
}