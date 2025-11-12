package com.bookmyturf.controller;

import com.bookmyturf.exception.GlobalExceptionHandler;
import com.bookmyturf.models.SportResponseDTO;
import com.bookmyturf.models.SportsFilterRequest;
import com.bookmyturf.service.SportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class FilterController {

    private final SportsService sportsService;

    @PostMapping("/filter")
    public ResponseEntity<?> filterSports(@RequestBody SportsFilterRequest filterRequest) {
        List<SportResponseDTO> sports = sportsService.filterSports(filterRequest);
        return GlobalExceptionHandler.GoodResponse(
                HttpStatus.OK,
                "Filtered sports fetched successfully",
                sports
        );
    }
}
