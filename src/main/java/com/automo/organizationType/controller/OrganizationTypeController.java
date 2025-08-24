package com.automo.organizationType.controller;

import com.automo.organizationType.dto.OrganizationTypeDto;
import com.automo.organizationType.response.OrganizationTypeResponse;
import com.automo.organizationType.service.OrganizationTypeService;
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
@RequestMapping("/organization-types")
@RequiredArgsConstructor
@Tag(name = "Organization Types", description = "Organization type management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrganizationTypeController {

    private final OrganizationTypeService organizationTypeService;

    @Operation(description = "List all organization types", summary = "Get all organization types")
    @ApiResponse(responseCode = "200", description = "Organization types retrieved successfully")
    @GetMapping
    public ResponseEntity<List<OrganizationTypeResponse>> getAllOrganizationTypes() {
        return ResponseEntity.ok(organizationTypeService.getAllOrganizationTypes());
    }

    @Operation(description = "Get organization type by ID", summary = "Get a specific organization type by ID")
    @ApiResponse(responseCode = "200", description = "Organization type retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationTypeResponse> getOrganizationTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(organizationTypeService.getOrganizationTypeByIdResponse(id));
    }

    @Operation(description = "Create new organization type", summary = "Create a new organization type")
    @ApiResponse(responseCode = "201", description = "Organization type created successfully")
    @PostMapping
    public ResponseEntity<OrganizationTypeResponse> createOrganizationType(@Valid @RequestBody OrganizationTypeDto organizationTypeDto) {
        OrganizationTypeResponse response = organizationTypeService.createOrganizationType(organizationTypeDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update organization type", summary = "Update an existing organization type")
    @ApiResponse(responseCode = "200", description = "Organization type updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationTypeResponse> updateOrganizationType(@PathVariable Long id, @Valid @RequestBody OrganizationTypeDto organizationTypeDto) {
        return ResponseEntity.ok(organizationTypeService.updateOrganizationType(id, organizationTypeDto));
    }

    @Operation(description = "Delete organization type", summary = "Delete an organization type")
    @ApiResponse(responseCode = "204", description = "Organization type deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizationType(@PathVariable Long id) {
        organizationTypeService.deleteOrganizationType(id);
        return ResponseEntity.noContent().build();
    }
} 