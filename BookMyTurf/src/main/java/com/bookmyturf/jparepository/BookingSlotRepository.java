package com.bookmyturf.jparepository;


import com.bookmyturf.entity.BookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {

    @Query("SELECT bs FROM BookingSlot bs WHERE bs.slotTiming.id = :slotTimingId AND bs.status <> 'AVAILABLE'")
    Optional<BookingSlot> findActiveSlot(Long slotTimingId);
}