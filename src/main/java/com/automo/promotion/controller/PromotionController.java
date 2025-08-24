package com.automo.promotion.controller;

import com.automo.promotion.dto.PromotionDto;
import com.automo.promotion.response.PromotionResponse;
import com.automo.promotion.service.PromotionService;
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
@RequestMapping("/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Promotion management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PromotionController {

    private final PromotionService promotionService;

    @Operation(description = "List all promotions", summary = "Get all promotions")
    @ApiResponse(responseCode = "200", description = "Promotions retrieved successfully")
    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @Operation(description = "Get promotion by ID", summary = "Get a specific promotion by ID")
    @ApiResponse(responseCode = "200", description = "Promotion retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponse> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionByIdResponse(id));
    }

    @Operation(description = "Get promotion by code", summary = "Get a specific promotion by code")
    @ApiResponse(responseCode = "200", description = "Promotion retrieved successfully")
    @GetMapping("/code/{code}")
    public ResponseEntity<PromotionResponse> getPromotionByCode(@PathVariable String code) {
        return ResponseEntity.ok(promotionService.getPromotionByCode(code));
    }

    @Operation(description = "Create new promotion", summary = "Create a new promotion")
    @ApiResponse(responseCode = "201", description = "Promotion created successfully")
    @PostMapping
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody PromotionDto promotionDto) {
        PromotionResponse response = promotionService.createPromotion(promotionDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update promotion", summary = "Update an existing promotion")
    @ApiResponse(responseCode = "200", description = "Promotion updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponse> updatePromotion(@PathVariable Long id, @Valid @RequestBody PromotionDto promotionDto) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, promotionDto));
    }

    @Operation(description = "Delete promotion", summary = "Delete a promotion")
    @ApiResponse(responseCode = "204", description = "Promotion deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get promotions by state", summary = "Get promotions filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Promotions retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<PromotionResponse>> getPromotionsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(promotionService.getPromotionsByState(stateId));
    }
} 