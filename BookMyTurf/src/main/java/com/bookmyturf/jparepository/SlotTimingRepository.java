package com.bookmyturf.jparepository;

import com.bookmyturf.entity.SlotTiming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SlotTimingRepository extends JpaRepository<SlotTiming, Long> {

    @Query("""
        SELECT s FROM SlotTiming s 
        WHERE s.sports.id = :sportId 
        AND s.id NOT IN (
            SELECT bs.slotTiming.id FROM BookingSlot bs 
            WHERE bs.status IN ('BOOKED', 'TEMP_BLOCKED')
        )
    """)
    List<SlotTiming> findAvailableSlots(Long sportId);
}
