package com.automo.deal.controller;

import com.automo.deal.dto.DealDto;
import com.automo.deal.response.DealResponse;
import com.automo.deal.service.DealService;
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
@RequestMapping("/deals")
@RequiredArgsConstructor
@Tag(name = "Deals", description = "Deal management APIs")
@SecurityRequirement(name = "bearerAuth")
public class DealController {

    private final DealService dealService;

    @Operation(description = "List all deals", summary = "Get all deals")
    @ApiResponse(responseCode = "200", description = "Deals retrieved successfully")
    @GetMapping
    public ResponseEntity<List<DealResponse>> getAllDeals() {
        return ResponseEntity.ok(dealService.getAllDeals());
    }

    @Operation(description = "Get deal by ID", summary = "Get a specific deal by ID")
    @ApiResponse(responseCode = "200", description = "Deal retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<DealResponse> getDealById(@PathVariable Long id) {
        return ResponseEntity.ok(dealService.getDealByIdResponse(id));
    }

    @Operation(description = "Create new deal", summary = "Create a new deal")
    @ApiResponse(responseCode = "201", description = "Deal created successfully")
    @PostMapping
    public ResponseEntity<DealResponse> createDeal(@Valid @RequestBody DealDto dealDto) {
        DealResponse response = dealService.createDeal(dealDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update deal", summary = "Update an existing deal")
    @ApiResponse(responseCode = "200", description = "Deal updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<DealResponse> updateDeal(@PathVariable Long id, @Valid @RequestBody DealDto dealDto) {
        return ResponseEntity.ok(dealService.updateDeal(id, dealDto));
    }

    @Operation(description = "Delete deal", summary = "Delete a deal")
    @ApiResponse(responseCode = "204", description = "Deal deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeal(@PathVariable Long id) {
        dealService.deleteDeal(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get deals by state", summary = "Get deals filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Deals retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<DealResponse>> getDealsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(dealService.getDealsByState(stateId));
    }

    @Operation(description = "Get deals by identifier", summary = "Get deals filtered by identifier ID")
    @ApiResponse(responseCode = "200", description = "Deals retrieved successfully")
    @GetMapping("/identifier/{identifierId}")
    public ResponseEntity<List<DealResponse>> getDealsByIdentifier(@PathVariable Long identifierId) {
        return ResponseEntity.ok(dealService.getDealsByIdentifier(identifierId));
    }

    @Operation(description = "Get deals by lead", summary = "Get deals filtered by lead ID")
    @ApiResponse(responseCode = "200", description = "Deals retrieved successfully")
    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<DealResponse>> getDealsByLead(@PathVariable Long leadId) {
        return ResponseEntity.ok(dealService.getDealsByLead(leadId));
    }

    @Operation(description = "Get deals by promotion", summary = "Get deals filtered by promotion ID")
    @ApiResponse(responseCode = "200", description = "Deals retrieved successfully")
    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<List<DealResponse>> getDealsByPromotion(@PathVariable Long promotionId) {
        return ResponseEntity.ok(dealService.getDealsByPromotion(promotionId));
    }
} 