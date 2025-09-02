package com.automo.admin.controller;

import com.automo.admin.dto.AdminDto;
import com.automo.admin.response.AdminResponse;
import com.automo.admin.service.AdminService;
import com.automo.model.dto.PaginatedResponse;
import com.automo.model.dto.PaginationRequest;
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

    @Operation(description = "Get paginated list of admins with search", 
               summary = "Get paginated admins with optional search")
    @ApiResponse(responseCode = "200", description = "Paginated admins retrieved successfully")
    @GetMapping("/paginated")
    public ResponseEntity<PaginatedResponse<AdminResponse>> getAdminsPaginated(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        PaginationRequest request = new PaginationRequest(search, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(adminService.getEntitiesPaginated(request));
    }
} 