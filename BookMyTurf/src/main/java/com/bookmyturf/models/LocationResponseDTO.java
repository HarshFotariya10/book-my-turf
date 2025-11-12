package com.bookmyturf.models;

import lombok.Data;
import java.util.List;

@Data
public class LocationResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private List<LocationMediaDTO> mediaFiles;
}
