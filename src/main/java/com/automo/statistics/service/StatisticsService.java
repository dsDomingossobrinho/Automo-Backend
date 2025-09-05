package com.automo.statistics.service;

import com.automo.statistics.dto.RevenueStatisticsResponse;

public interface StatisticsService {
    
    /**
     * Obtém estatísticas completas de faturação
     */
    RevenueStatisticsResponse getRevenueStatistics();
    
    /**
     * Obtém estatísticas de faturação para um período específico
     */
    RevenueStatisticsResponse getRevenueStatistics(Long stateId);
}