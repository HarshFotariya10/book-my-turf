package com.bookmyturf.models;

import lombok.Data;

import java.util.List;

@Data
public class SportResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Long locationId;
    private String locationName;
    private List<MediaFileDTO> mediaFiles;

    @Data
    public static class MediaFileDTO {
        private String fileType;
        private String path;
    }
}
