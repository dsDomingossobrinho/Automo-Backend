package com.automo.leadType.controller;

import com.automo.leadType.dto.LeadTypeDto;
import com.automo.leadType.response.LeadTypeResponse;
import com.automo.leadType.service.LeadTypeService;
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
@RequestMapping("/lead-types")
@RequiredArgsConstructor
@Tag(name = "Lead Types", description = "Lead type management APIs")
@SecurityRequirement(name = "bearerAuth")
public class LeadTypeController {

    private final LeadTypeService leadTypeService;

    @Operation(description = "List all lead types", summary = "Get all lead types")
    @ApiResponse(responseCode = "200", description = "Lead types retrieved successfully")
    @GetMapping
    public ResponseEntity<List<LeadTypeResponse>> getAllLeadTypes() {
        return ResponseEntity.ok(leadTypeService.getAllLeadTypes());
    }

    @Operation(description = "Get lead type by ID", summary = "Get a specific lead type by ID")
    @ApiResponse(responseCode = "200", description = "Lead type retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<LeadTypeResponse> getLeadTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(leadTypeService.getLeadTypeByIdResponse(id));
    }

    @Operation(description = "Create new lead type", summary = "Create a new lead type")
    @ApiResponse(responseCode = "201", description = "Lead type created successfully")
    @PostMapping
    public ResponseEntity<LeadTypeResponse> createLeadType(@Valid @RequestBody LeadTypeDto leadTypeDto) {
        LeadTypeResponse response = leadTypeService.createLeadType(leadTypeDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update lead type", summary = "Update an existing lead type")
    @ApiResponse(responseCode = "200", description = "Lead type updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<LeadTypeResponse> updateLeadType(@PathVariable Long id, @Valid @RequestBody LeadTypeDto leadTypeDto) {
        return ResponseEntity.ok(leadTypeService.updateLeadType(id, leadTypeDto));
    }

    @Operation(description = "Delete lead type", summary = "Delete a lead type")
    @ApiResponse(responseCode = "204", description = "Lead type deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeadType(@PathVariable Long id) {
        leadTypeService.deleteLeadType(id);
        return ResponseEntity.noContent().build();
    }
} 