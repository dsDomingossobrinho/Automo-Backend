package com.automo.subscriptionPlan.controller;

import com.automo.subscriptionPlan.dto.SubscriptionPlanDto;
import com.automo.subscriptionPlan.response.SubscriptionPlanResponse;
import com.automo.subscriptionPlan.service.SubscriptionPlanService;
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
@RequestMapping("/subscription-plans")
@RequiredArgsConstructor
@Tag(name = "Subscription Plans", description = "Subscription plan management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @Operation(description = "List all subscription plans", summary = "Get all subscription plans")
    @ApiResponse(responseCode = "200", description = "Subscription plans retrieved successfully")
    @GetMapping
    public ResponseEntity<List<SubscriptionPlanResponse>> getAllSubscriptionPlans() {
        return ResponseEntity.ok(subscriptionPlanService.getAllSubscriptionPlans());
    }

    @Operation(description = "Get subscription plan by ID", summary = "Get a specific subscription plan by ID")
    @ApiResponse(responseCode = "200", description = "Subscription plan retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanResponse> getSubscriptionPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionPlanService.getSubscriptionPlanByIdResponse(id));
    }

    @Operation(description = "Create new subscription plan", summary = "Create a new subscription plan")
    @ApiResponse(responseCode = "201", description = "Subscription plan created successfully")
    @PostMapping
    public ResponseEntity<SubscriptionPlanResponse> createSubscriptionPlan(@Valid @RequestBody SubscriptionPlanDto subscriptionPlanDto) {
        SubscriptionPlanResponse response = subscriptionPlanService.createSubscriptionPlan(subscriptionPlanDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update subscription plan", summary = "Update an existing subscription plan")
    @ApiResponse(responseCode = "200", description = "Subscription plan updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanResponse> updateSubscriptionPlan(@PathVariable Long id, @Valid @RequestBody SubscriptionPlanDto subscriptionPlanDto) {
        return ResponseEntity.ok(subscriptionPlanService.updateSubscriptionPlan(id, subscriptionPlanDto));
    }

    @Operation(description = "Delete subscription plan", summary = "Delete a subscription plan")
    @ApiResponse(responseCode = "204", description = "Subscription plan deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriptionPlan(@PathVariable Long id) {
        subscriptionPlanService.deleteSubscriptionPlan(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get subscription plans by state", summary = "Get subscription plans filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Subscription plans retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<SubscriptionPlanResponse>> getSubscriptionPlansByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(subscriptionPlanService.getSubscriptionPlansByState(stateId));
    }
} 