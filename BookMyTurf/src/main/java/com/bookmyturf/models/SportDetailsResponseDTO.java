package com.bookmyturf.models;

import lombok.Data;
import java.util.List;

@Data
public class SportDetailsResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private String locationName;
    private List<DayWiseSlotsDTO> weekSlots;   // ✅ Changed field
    private List<MediaFileDTO> mediaFiles;

    @Data
    public static class DayWiseSlotsDTO {
        private String day;               // e.g. "WEDNESDAY"
        private String date;              // e.g. "2025-11-13"
        private List<SlotTimingDTO> slots; // ✅ All slots for this day
    }

    @Data
    public static class SlotTimingDTO {
        private Long slotId;         // ✅ Add this (SlotTiming primary key)
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private double price;
        private String status;
    }

    @Data
    public static class MediaFileDTO {
        private String fileType;
        private String base64Data;  // ✅ for image/video preview
    }
}
