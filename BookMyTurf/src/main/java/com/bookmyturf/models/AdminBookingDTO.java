package com.bookmyturf.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminBookingDTO  {

    private Long bookingId;
    private Long userId;
    private String userName;
    private String sportName;
    private String locationName;
    private Double totalAmount;
    private LocalDateTime bookingTime;
}
