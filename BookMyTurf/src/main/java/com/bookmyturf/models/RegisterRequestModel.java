package com.bookmyturf.models;

import com.bookmyturf.constraints.Roles;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequestModel {

    private String firstName;
    private String lastName;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email;


    @NotBlank(message = "Contact number is required")
    private String contact;


    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be 6-20 characters long")
    String password;

    private Roles role;
    private String otp;
}
