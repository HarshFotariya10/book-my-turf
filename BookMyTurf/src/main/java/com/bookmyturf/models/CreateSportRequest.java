package com.bookmyturf.models;

import lombok.Data;

import java.util.List;

@Data
public class CreateSportRequest {

    private String name;
    private String description;
    private Long categoryId;

    // Media files (base64 strings, like your LocationDTO)
    private List<MediaFileDTO> mediaFiles;

    // Operational hours and dynamic price ranges per day
    private List<DaySlotDTO> daySlots;

    @Data
    public static class MediaFileDTO {
        private String fileType; // "image" or "video"
        private String base64Data; // base64 string from frontend
    }

    @Data
    public static class DaySlotDTO {
        private String day; // MONDAY, TUESDAY, etc.
        private int openHour; // location opening hour (0-23)
        private int closeHour; // location closing hour (exclusive)
        private List<TimeSlotDTO> slots; // dynamic pricing ranges within open-close
    }

    @Data
    public static class TimeSlotDTO {
        private int startHour; // must be within open-close
        private int endHour;   // exclusive
        private double price;  // price for this time range
    }
}
