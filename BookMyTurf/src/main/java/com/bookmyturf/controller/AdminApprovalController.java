package com.bookmyturf.controller;

import com.bookmyturf.constraints.Roles;
import com.bookmyturf.entity.User;
import com.bookmyturf.jparepository.UserJpaRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Approval", description = "SUPER_ADMIN approves admins")
public class AdminApprovalController {

    @Autowired
    private UserJpaRepository userRepo;

    // Get all pending admin requests
    @GetMapping("/pending-admins")
    public ResponseEntity<?> getPendingAdmins() {
        // Find all users with role ADMIN and isActive = false
        List<User> pendingAdmins = userRepo.findByRoleAndIsActiveFalse(Roles.ADMIN);

        return ResponseEntity.ok(pendingAdmins);
    }

    // Approve admin
    @PostMapping("/approve/{adminId}")
    public ResponseEntity<?> approveAdmin(@PathVariable Long adminId) {
        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Only ADMIN users can be approved");
        }

        admin.setActive(true);
        userRepo.save(admin);
        return ResponseEntity.ok("Admin approved successfully");
    }
}
