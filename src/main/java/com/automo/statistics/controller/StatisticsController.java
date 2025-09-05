package com.automo.statistics.controller;

import com.automo.statistics.dto.RevenueStatisticsResponse;
import com.automo.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Revenue and business statistics APIs")
@SecurityRequirement(name = "bearerAuth")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(
        summary = "Get revenue statistics", 
        description = "Returns complete revenue statistics including total, daily, monthly and semester revenue"
    )
    @ApiResponse(responseCode = "200", description = "Revenue statistics retrieved successfully")
    @GetMapping("/revenue")
    public ResponseEntity<RevenueStatisticsResponse> getRevenueStatistics() {
        RevenueStatisticsResponse statistics = statisticsService.getRevenueStatistics();
        return ResponseEntity.ok(statistics);
    }

    @Operation(
        summary = "Get revenue statistics by state", 
        description = "Returns revenue statistics filtered by payment state (e.g., approved payments only)"
    )
    @ApiResponse(responseCode = "200", description = "Revenue statistics by state retrieved successfully")
    @GetMapping("/revenue/state/{stateId}")
    public ResponseEntity<RevenueStatisticsResponse> getRevenueStatisticsByState(
            @Parameter(description = "Payment state ID to filter by", required = true)
            @PathVariable Long stateId) {
        
        RevenueStatisticsResponse statistics = statisticsService.getRevenueStatistics(stateId);
        return ResponseEntity.ok(statistics);
    }
}