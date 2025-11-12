package com.bookmyturf.controller;

import com.bookmyturf.entity.Sports;
import com.bookmyturf.exception.GlobalExceptionHandler;
import com.bookmyturf.models.*;
import com.bookmyturf.service.SportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations/sports/")
@RequiredArgsConstructor
@Tag(name = "Sports APIs", description = "APIs for managing sports under a location")
public class SportsController {

    private final SportsService sportsService;

    @Operation(summary = "Create a new sport under a specific location", responses = {
            @ApiResponse(responseCode = "200", description = "Sport created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("{locationId}/createsports")
    public ResponseEntity<?> createSport(
            @PathVariable Long locationId,
             @RequestBody CreateSportRequest request) {

        SportResponseDTO sport = sportsService.createSport(locationId, request);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK, "Sport created successfully", sport);
    }
    @Operation(summary = "Fetch all unique categories available in a city")
    @GetMapping("/by-city")
    public ResponseEntity<?> getCategoriesByCity(@RequestParam String city) {
        List<CategoryResponseDTO> categories = sportsService.getCategoriesByCity(city);

        return GlobalExceptionHandler.GoodResponse(
                HttpStatus.OK,
                "Categories fetched successfully for city: " + city,
                categories
        );
    }
    @Operation(summary = "Fetch full sport details by ID (description, slots, and media)")
    @GetMapping("/{sportId}/details")
    public ResponseEntity<?> getSportDetails(@PathVariable Long sportId) {
        SportDetailsResponseDTO sportDetails = sportsService.getSportDetailsById(sportId);
        return GlobalExceptionHandler.GoodResponse(
                HttpStatus.OK,
                "Sport details fetched successfully",
                sportDetails
        );
    }

    @Operation(summary = "Fetch all sports by city and category")
    @GetMapping("/by-city-category")
    public ResponseEntity<?> getSportsByCityAndCategory(
            @RequestParam String city,
            @RequestParam String categoryName) {

        List<SportResponseDTO> sports = sportsService.getSportsByCityAndCategory(city, categoryName);

        return GlobalExceptionHandler.GoodResponse(
                HttpStatus.OK,
                "Sports fetched successfully for city: " + city + " and category: " + categoryName,
                sports
        );
    }

}