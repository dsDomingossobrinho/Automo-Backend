package com.automo.statistics.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RevenueStatisticsResponse(
    BigDecimal totalRevenue,
    BigDecimal dailyRevenue,
    BigDecimal monthlyRevenue,
    BigDecimal semesterRevenue,
    LocalDateTime calculatedAt,
    String currency
) {
    public static RevenueStatisticsResponse of(
        BigDecimal totalRevenue,
        BigDecimal dailyRevenue,
        BigDecimal monthlyRevenue,
        BigDecimal semesterRevenue
    ) {
        return new RevenueStatisticsResponse(
            totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
            dailyRevenue != null ? dailyRevenue : BigDecimal.ZERO,
            monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO,
            semesterRevenue != null ? semesterRevenue : BigDecimal.ZERO,
            LocalDateTime.now(),
            "EUR"
        );
    }
}