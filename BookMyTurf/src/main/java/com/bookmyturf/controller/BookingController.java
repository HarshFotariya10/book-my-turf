package com.bookmyturf.controller;

import com.bookmyturf.entity.Booking;
import com.bookmyturf.exception.GlobalExceptionHandler;
import com.bookmyturf.security.JwtUtil;
import com.bookmyturf.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking APIs", description = "APIs for creating, confirming, cancelling and viewing bookings")
public class BookingController {

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;


    @Operation(summary = "Create a new booking (temporarily lock slots for 10 minutes)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Booking created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request or slot unavailable")
            })
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createBooking(
            @RequestBody List<Long> slotTimingIds, HttpServletRequest request) {

        Booking booking = bookingService.createBooking(fetchUserIdFromToken(request), slotTimingIds);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK,
                "Booking created successfully (slots temporarily locked for 10 minutes)", booking);
    }
    @PostMapping("/confirm/{bookingId}")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.confirmBooking(bookingId);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK,
                "Booking confirmed successfully", booking);
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK,
                "Booking cancelled successfully", booking);
    }

    public Long fetchUserIdFromToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return  jwtUtil.extractUserId(token);
    }
}
