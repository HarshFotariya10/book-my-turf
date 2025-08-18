package com.bookmyturf.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
public class SlotTiming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;  // MONDAY to SUNDAY

    private LocalTime startTime;

    private LocalTime endTime;

    private double price;

    @ManyToOne
    @JoinColumn(name = "sports_id")
    private Sports sports;
}
