package com.automo.subscription.controller;

import com.automo.subscription.dto.SubscriptionDto;
import com.automo.subscription.response.SubscriptionResponse;
import com.automo.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Subscription management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(description = "List all subscriptions", summary = "Get all subscriptions")
    @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @Operation(description = "Get subscription by ID", summary = "Get a specific subscription by ID")
    @ApiResponse(responseCode = "200", description = "Subscription retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionByIdResponse(id));
    }

    @Operation(description = "Create new subscription", summary = "Create a new subscription")
    @ApiResponse(responseCode = "201", description = "Subscription created successfully")
    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(@Valid @RequestBody SubscriptionDto subscriptionDto) {
        SubscriptionResponse response = subscriptionService.createSubscription(subscriptionDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update subscription", summary = "Update an existing subscription")
    @ApiResponse(responseCode = "200", description = "Subscription updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> updateSubscription(@PathVariable Long id, @Valid @RequestBody SubscriptionDto subscriptionDto) {
        return ResponseEntity.ok(subscriptionService.updateSubscription(id, subscriptionDto));
    }

    @Operation(description = "Delete subscription", summary = "Delete a subscription")
    @ApiResponse(responseCode = "204", description = "Subscription deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get subscriptions by state", summary = "Get subscriptions filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByState(stateId));
    }

    @Operation(description = "Get subscriptions by user", summary = "Get subscriptions filtered by user ID")
    @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByUser(userId));
    }

    @Operation(description = "Get subscriptions by plan", summary = "Get subscriptions filtered by plan ID")
    @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByPlan(planId));
    }

    @Operation(description = "Get subscriptions by promotion", summary = "Get subscriptions filtered by promotion ID")
    @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByPromotion(@PathVariable Long promotionId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByPromotion(promotionId));
    }

    @Operation(description = "Get subscriptions by date range", summary = "Get subscriptions within a date range")
    @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    @GetMapping("/date-range")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByDateRange(startDate, endDate));
    }

    @Operation(description = "Get expired subscriptions", summary = "Get subscriptions that expired before a specific date")
    @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully")
    @GetMapping("/expired")
    public ResponseEntity<List<SubscriptionResponse>> getExpiredSubscriptions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(subscriptionService.getExpiredSubscriptions(date));
    }
} 