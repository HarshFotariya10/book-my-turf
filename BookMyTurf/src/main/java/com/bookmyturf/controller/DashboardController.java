package com.bookmyturf.controller;

import com.bookmyturf.models.DashboardResponse;
import com.bookmyturf.security.JwtUtil;
import com.bookmyturf.service.implementation.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard APIs", description = "API to get admin dashboard statistics")
public class DashboardController {

    private final DashboardService dashboardService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Get dashboard stats for admin user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dashboard fetched successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    @GetMapping("/get-dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(HttpServletRequest request) {
        Long userId = fetchUserIdFromToken(request);

        DashboardResponse response = dashboardService.getDashboard(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public Long fetchUserIdFromToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return  jwtUtil.extractUserId(token);
    }
}