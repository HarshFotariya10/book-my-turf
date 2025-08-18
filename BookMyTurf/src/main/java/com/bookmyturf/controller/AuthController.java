package com.bookmyturf.controller;

import com.bookmyturf.entity.User;
import com.bookmyturf.exception.GlobalExceptionHandler;
import com.bookmyturf.models.JwtResponse;
import com.bookmyturf.models.LoginRequestModel;
import com.bookmyturf.models.RegisterRequestModel;
import com.bookmyturf.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication APIs", description = "Handles login, registration, OTP")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Send OTP to user's email", responses = {
            @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        String message = authService.sendOtp(email);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK, message, null);
    }

    @Operation(summary = "Register user with OTP verification", responses = {
            @ApiResponse(responseCode = "200", description = "User registered"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP or data")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestModel request) {
        User user = authService.register(request);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK,"Regiter Successfully ",user);
    }

    @Operation(summary = "Login user and return JWT token", responses = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JwtResponse.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestModel request) {
        JwtResponse jwt = authService.login(request);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK, "Login successful", jwt);
    }
}
