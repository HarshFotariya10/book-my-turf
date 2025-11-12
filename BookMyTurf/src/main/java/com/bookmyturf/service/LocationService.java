package com.bookmyturf.service;

import com.bookmyturf.entity.Category;
import com.bookmyturf.entity.Location;
import com.bookmyturf.entity.LocationMedia;
import com.bookmyturf.models.LocationDTO;
import com.bookmyturf.models.LocationResponseDTO;

import java.util.List;

public interface LocationService {
    LocationResponseDTO createLocation(LocationDTO location);
    LocationResponseDTO updateLocation(Long id, LocationDTO location);
    LocationResponseDTO deleteLocation(Long id);
    LocationResponseDTO getLocationById(Long id);
    List<LocationResponseDTO> getAllLocationsByAdmin();

    Category addCategoryToLocation(Long locationId,String Name);
}
