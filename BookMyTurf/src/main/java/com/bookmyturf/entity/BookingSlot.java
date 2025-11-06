package com.bookmyturf.entity;

import com.bookmyturf.constraints.SlotStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "slot_timing_id")
    private SlotTiming slotTiming;

    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.TEMP_BLOCKED;

    private LocalDateTime lockedAt = LocalDateTime.now();
}
