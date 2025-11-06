package com.bookmyturf.service;

import com.bookmyturf.entity.Booking;

import java.util.List;

public interface BookingService {

     Booking createBooking(Long userId, List<Long> slotTimingIds);
     Booking confirmBooking(Long bookingId);

     Booking cancelBooking(Long bookingId);

     void expireBookings();

    }
