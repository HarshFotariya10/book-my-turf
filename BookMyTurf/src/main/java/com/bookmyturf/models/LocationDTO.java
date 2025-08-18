package com.bookmyturf.models;

import com.bookmyturf.entity.LocationMedia;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class LocationDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String pincode;

    private Long adminId;

    private List<LocationMediaDTO> mediaFiles;


}
