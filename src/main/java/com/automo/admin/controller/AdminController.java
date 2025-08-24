package com.automo.admin.controller;

import com.automo.admin.dto.AdminDto;
import com.automo.admin.response.AdminResponse;
import com.automo.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
@Tag(name = "Admins", description = "Admin management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @Operation(description = "List all admins", summary = "Get all admins")
    @ApiResponse(responseCode = "200", description = "Admins retrieved successfully")
    @GetMapping
    public ResponseEntity<List<AdminResponse>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @Operation(description = "Get admin by ID", summary = "Get a specific admin by ID")
    @ApiResponse(responseCode = "200", description = "Admin retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> getAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAdminByIdResponse(id));
    }

    @Operation(description = "Get admin by email", summary = "Get a specific admin by email")
    @ApiResponse(responseCode = "200", description = "Admin retrieved successfully")
    @GetMapping("/email/{email}")
    public ResponseEntity<AdminResponse> getAdminByEmail(@PathVariable String email) {
        return ResponseEntity.ok(adminService.getAdminByEmail(email));
    }

    @Operation(description = "Get admin by auth ID", summary = "Get a specific admin by authentication ID")
    @ApiResponse(responseCode = "200", description = "Admin retrieved successfully")
    @GetMapping("/auth/{authId}")
    public ResponseEntity<AdminResponse> getAdminByAuthId(@PathVariable Long authId) {
        return ResponseEntity.ok(adminService.getAdminByAuthId(authId));
    }

    @Operation(description = "Create new admin", summary = "Create a new admin")
    @ApiResponse(responseCode = "201", description = "Admin created successfully")
    @PostMapping
    public ResponseEntity<AdminResponse> createAdmin(@Valid @RequestBody AdminDto adminDto) {
        AdminResponse response = adminService.createAdmin(adminDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update admin", summary = "Update an existing admin")
    @ApiResponse(responseCode = "200", description = "Admin updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminDto adminDto) {
        return ResponseEntity.ok(adminService.updateAdmin(id, adminDto));
    }

    @Operation(description = "Delete admin", summary = "Delete an admin")
    @ApiResponse(responseCode = "204", description = "Admin deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get admins by state", summary = "Get admins filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Admins retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<AdminResponse>> getAdminsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(adminService.getAdminsByState(stateId));
    }
} 