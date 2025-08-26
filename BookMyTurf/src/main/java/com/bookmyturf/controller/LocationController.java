package com.bookmyturf.controller;

import com.bookmyturf.entity.Location;
import com.bookmyturf.exception.GlobalExceptionHandler;
import com.bookmyturf.models.LocationDTO;
import com.bookmyturf.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@Tag(name = "Location APIs", description = "Handles CRUD operations for Locations")
public class LocationController {

    @Autowired
    private LocationService locationService;
    @Operation(summary = "Create new location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Location created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LocationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> createLocation(@Valid @RequestBody LocationDTO location) {
        System.out.println(location);
        Location saved = locationService.createLocation(location);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK, "Location created successfully", saved);
    }

    @Operation(summary = "Update a location", description = "Updates location details by ID for the logged-in admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LocationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocation(@PathVariable Long id,
                                            @RequestBody LocationDTO updatedLocation) {
        Location updated = locationService.updateLocation(id, updatedLocation);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK, "Location updated successfully", updated);
    }
    @Operation(summary = "Get a location by ID", description = "Returns details of a specific location by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Location.class))),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getLocation(@PathVariable Long id) {
        LocationDTO location = locationService.getLocationById(id);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK, "Location fetched", location);
    }

    @Operation(summary = "Delete a location", description = "Deletes a location by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK, "Location deleted", null);
    }
    @Operation(summary = "Get all locations", description = "Returns a list of all locations for the current admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of locations returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Location.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllByAdmin() {
        List<Location> locations = locationService.getAllLocationsByAdmin();
        return GlobalExceptionHandler.GoodResponse(HttpStatus.OK, "Locations fetched", locations);
    }
    @Operation(summary = "Add Category to a Location",
            description = "Adds a new category under a specific location.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category added successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.bookmyturf.entity.Category.class))),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    @PostMapping("/{locationId}/category")
    public ResponseEntity<?> addCategoryToLocation(@PathVariable Long locationId,
                                                   @RequestParam String categoryName) {
        var category = locationService.addCategoryToLocation(locationId, categoryName);
        return GlobalExceptionHandler.GoodResponse(HttpStatus.CREATED, "Category added successfully", category);
    }


}
