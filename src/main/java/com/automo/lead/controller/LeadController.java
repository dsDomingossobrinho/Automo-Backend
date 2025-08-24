package com.automo.lead.controller;

import com.automo.lead.dto.LeadDto;
import com.automo.lead.response.LeadResponse;
import com.automo.lead.service.LeadService;
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
@RequestMapping("/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Lead management APIs")
@SecurityRequirement(name = "bearerAuth")
public class LeadController {

    private final LeadService leadService;

    @Operation(description = "List all leads", summary = "Get all leads")
    @ApiResponse(responseCode = "200", description = "Leads retrieved successfully")
    @GetMapping
    public ResponseEntity<List<LeadResponse>> getAllLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }

    @Operation(description = "Get lead by ID", summary = "Get a specific lead by ID")
    @ApiResponse(responseCode = "200", description = "Lead retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> getLeadById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadByIdResponse(id));
    }

    @Operation(description = "Create new lead", summary = "Create a new lead")
    @ApiResponse(responseCode = "201", description = "Lead created successfully")
    @PostMapping
    public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody LeadDto leadDto) {
        LeadResponse response = leadService.createLead(leadDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update lead", summary = "Update an existing lead")
    @ApiResponse(responseCode = "200", description = "Lead updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<LeadResponse> updateLead(@PathVariable Long id, @Valid @RequestBody LeadDto leadDto) {
        return ResponseEntity.ok(leadService.updateLead(id, leadDto));
    }

    @Operation(description = "Delete lead", summary = "Delete a lead")
    @ApiResponse(responseCode = "204", description = "Lead deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get leads by state", summary = "Get leads filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Leads retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<LeadResponse>> getLeadsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(leadService.getLeadsByState(stateId));
    }

    @Operation(description = "Get leads by lead type", summary = "Get leads filtered by lead type ID")
    @ApiResponse(responseCode = "200", description = "Leads retrieved successfully")
    @GetMapping("/type/{leadTypeId}")
    public ResponseEntity<List<LeadResponse>> getLeadsByLeadType(@PathVariable Long leadTypeId) {
        return ResponseEntity.ok(leadService.getLeadsByLeadType(leadTypeId));
    }

    @Operation(description = "Get leads by identifier", summary = "Get leads filtered by identifier ID")
    @ApiResponse(responseCode = "200", description = "Leads retrieved successfully")
    @GetMapping("/identifier/{identifierId}")
    public ResponseEntity<List<LeadResponse>> getLeadsByIdentifier(@PathVariable Long identifierId) {
        return ResponseEntity.ok(leadService.getLeadsByIdentifier(identifierId));
    }
} 