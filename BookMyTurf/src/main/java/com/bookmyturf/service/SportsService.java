package com.bookmyturf.service;

import com.bookmyturf.entity.Sports;
import com.bookmyturf.models.*;

import java.util.List;

public interface SportsService {
    SportResponseDTO createSport(Long locationId, CreateSportRequest dto);
    public List<CategoryResponseDTO> getCategoriesByCity(String city);
    public List<SportResponseDTO> getSportsByCityAndCategory(String city, String categoryName);
    List<SportResponseDTO> filterSports(SportsFilterRequest filterRequest);

    SportDetailsResponseDTO getSportDetailsById(Long sportId);


}
