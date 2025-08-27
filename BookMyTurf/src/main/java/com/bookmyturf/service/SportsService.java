package com.bookmyturf.service;

import com.bookmyturf.entity.Sports;
import com.bookmyturf.models.CreateSportRequest;
import com.bookmyturf.models.SportResponseDTO;

public interface SportsService {
    SportResponseDTO createSport(Long locationId, CreateSportRequest dto);
}
