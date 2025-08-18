package com.bookmyturf.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MediaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileType; // "image" or "video"

    private String path;

    @ManyToOne
    @JoinColumn(name = "sports_id")
    private Sports sports;

}
