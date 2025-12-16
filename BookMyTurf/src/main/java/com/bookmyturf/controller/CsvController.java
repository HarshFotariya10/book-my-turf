package com.bookmyturf.controller;

import com.bookmyturf.models.AdminBookingDTO;
import com.bookmyturf.models.DashboardResponse;

import com.bookmyturf.security.JwtUtil;
import com.bookmyturf.service.BookingService;
import com.bookmyturf.service.implementation.CsvService;
import com.bookmyturf.service.implementation.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@AllArgsConstructor
public class CsvController {

    @Autowired
    private DashboardService dashboardService;

    private final JwtUtil jwtUtil;

    private final BookingService bookingService;

    @Autowired
    private CsvService csvService;
    public Long fetchUserIdFromToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return  jwtUtil.extractUserId(token);
    }
    @Operation(summary = "Download dashboard CSV for admin")
    @GetMapping("/api/dashboard/download-csv")
    public ResponseEntity<InputStreamResource> downloadDashboardCsv(HttpServletRequest request) {
        try {
            // Fetch admin user ID
            Long adminId = fetchUserIdFromToken(request);

            // Fetch dashboard stats
            DashboardResponse dashboard = dashboardService.getDashboard(adminId);

            // Create Excel in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String locationName = "All Locations"; // You can make this dynamic if needed
            csvService.generateDashboardExcel(dashboard, locationName, baos);

            // Convert to InputStream
            ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());

            // Filename with date
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String filename = "Dashboard_Stats_" + locationName.replaceAll("\\s+", "_") + "_" + date + ".xlsx";

            // Return as downloadable file
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(bis));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Download Super Admin confirmed bookings CSV")
    @GetMapping("/api/bookings/admin/download-csv")
    public ResponseEntity<InputStreamResource> downloadSuperAdminBookingCsv(HttpServletRequest request) {
        try {
            Long adminId = fetchUserIdFromToken(request);

            // Fetch all confirmed bookings
            List<AdminBookingDTO> bookings = bookingService.getConfirmedBookingsForAdmin(adminId);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String locationName = "All_Locations"; // Can be dynamic if needed

            csvService.generateBookingExcel(bookings, locationName, baos);

            ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());

            // Filename with date
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String filename = "Confirmed_Bookings_" + locationName + "_" + date + ".xlsx";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(bis));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

