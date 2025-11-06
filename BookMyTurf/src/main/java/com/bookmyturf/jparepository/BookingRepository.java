package com.bookmyturf.jparepository;

import com.bookmyturf.constraints.BookingStatus;
import com.bookmyturf.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatus(BookingStatus status);
}