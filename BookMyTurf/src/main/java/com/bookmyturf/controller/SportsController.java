package com.bookmyturf.controller;

import com.bookmyturf.entity.Sports;
import com.bookmyturf.exception.GlobalExceptionHandler;
import com.bookmyturf.models.CreateSportRequest;
import com.bookmyturf.models.SportResponseDTO;
import com.bookmyturf.service.SportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations/{locationId}/sports")
@RequiredArgsConstructor
@Tag(name = "Sports APIs", description = "APIs for managing sports under a location")
public class SportsController {

    private final SportsService sportsService;

    @Operation(summary = "Create a new sport under a specific location", responses = {
            @ApiResponse(responseCode = "200", description = "Sport created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<?> createSport(
            @PathVariable Long locationId,
             @RequestBody CreateSportRequest request) {

        SportResponseDTO sport = sportsService.createSport(locationId, request);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK, "Sport created successfully", sport);
    }
}