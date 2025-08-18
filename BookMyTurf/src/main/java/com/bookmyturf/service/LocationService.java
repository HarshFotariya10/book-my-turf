package com.bookmyturf.service;

import com.bookmyturf.entity.Location;
import com.bookmyturf.entity.LocationMedia;
import com.bookmyturf.models.LocationDTO;

import java.util.List;

public interface LocationService {
    Location createLocation(LocationDTO location);
    Location updateLocation(Long id, LocationDTO location);
    void deleteLocation(Long id);
    LocationDTO getLocationById(Long id);
    List<Location> getAllLocationsByAdmin();
}
