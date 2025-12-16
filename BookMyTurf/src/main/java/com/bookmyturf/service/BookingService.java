package com.bookmyturf.service;

import com.bookmyturf.entity.Booking;
import com.bookmyturf.models.AdminBookingDTO;

import java.util.List;

public interface BookingService {

     Booking createBooking(Long userId, List<Long> slotTimingIds);
     Booking confirmBooking(Long bookingId);
    List<AdminBookingDTO> getConfirmedBookingsForAdmin(Long adminId);
     Booking cancelBooking(Long bookingId);
    List<AdminBookingDTO> getAllConfirmedBookingsForSuperAdmin();


    void expireBookings();

    }
