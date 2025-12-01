package com.bookmyturf.jparepository;


import com.bookmyturf.constraints.SlotStatus;
import com.bookmyturf.entity.BookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {
    @Query("SELECT CASE WHEN COUNT(bs) > 0 THEN true ELSE false END " +
            "FROM BookingSlot bs WHERE bs.slotTiming.id = :slotTimingId " +
            "AND bs.status IN :statuses")
    boolean existsBySlotTimingIdAndStatusIn(Long slotTimingId, List<SlotStatus> statuses);
    @Query("SELECT bs FROM BookingSlot bs WHERE bs.slotTiming.id = :slotTimingId AND bs.status <> 'AVAILABLE'")
    Optional<BookingSlot> findActiveSlot(Long slotTimingId);
}