package com.bookmyturf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LocationMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileType; // image/video

    private String path;

    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonBackReference
    private Location location;
}
