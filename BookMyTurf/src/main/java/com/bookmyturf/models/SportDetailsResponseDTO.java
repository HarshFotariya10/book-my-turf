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
    private List<SlotTimingDTO> slotTimings;
    private List<MediaFileDTO> mediaFiles;

    @Data
    public static class SlotTimingDTO {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private double price;
    }

    @Data
    public static class MediaFileDTO {
        private String fileType;
        private String base64Data;  // ✅ for image/video preview
    }
}
