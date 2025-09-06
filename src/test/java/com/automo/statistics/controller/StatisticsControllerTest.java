package com.automo.statistics.controller;

import com.automo.config.security.JwtUtils;
import com.automo.statistics.dto.RevenueStatisticsResponse;
import com.automo.statistics.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@ActiveProfiles("test")
@DisplayName("Tests for StatisticsController")
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private JwtUtils jwtUtils;

    private RevenueStatisticsResponse statisticsResponse;

    @BeforeEach
    void setUp() {
        statisticsResponse = RevenueStatisticsResponse.of(
            new BigDecimal("50000.00"),    // Total revenue
            new BigDecimal("1500.00"),     // Daily revenue
            new BigDecimal("25000.00"),    // Monthly revenue
            new BigDecimal("150000.00")    // Semester revenue
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get revenue statistics successfully")
    void shouldGetRevenueStatisticsSuccessfully() throws Exception {
        // Given
        when(statisticsService.getRevenueStatistics()).thenReturn(statisticsResponse);

        // When & Then
        mockMvc.perform(get("/statistics/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(50000.00))
                .andExpect(jsonPath("$.dailyRevenue").value(1500.00))
                .andExpect(jsonPath("$.monthlyRevenue").value(25000.00))
                .andExpect(jsonPath("$.semesterRevenue").value(150000.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get revenue statistics by state successfully")
    void shouldGetRevenueStatisticsByStateSuccessfully() throws Exception {
        // Given
        Long stateId = 1L;
        RevenueStatisticsResponse stateStatistics = RevenueStatisticsResponse.of(
            new BigDecimal("30000.00"),    // Total revenue for state
            new BigDecimal("800.00"),      // Daily revenue for state
            new BigDecimal("15000.00"),    // Monthly revenue for state
            new BigDecimal("90000.00")     // Semester revenue for state
        );
        
        when(statisticsService.getRevenueStatistics(stateId)).thenReturn(stateStatistics);

        // When & Then
        mockMvc.perform(get("/statistics/revenue/state/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(30000.00))
                .andExpect(jsonPath("$.dailyRevenue").value(800.00))
                .andExpect(jsonPath("$.monthlyRevenue").value(15000.00))
                .andExpect(jsonPath("$.semesterRevenue").value(90000.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle zero revenue statistics")
    void shouldHandleZeroRevenueStatistics() throws Exception {
        // Given
        RevenueStatisticsResponse zeroStatistics = RevenueStatisticsResponse.of(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
        
        when(statisticsService.getRevenueStatistics()).thenReturn(zeroStatistics);

        // When & Then
        mockMvc.perform(get("/statistics/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(0.00))
                .andExpect(jsonPath("$.dailyRevenue").value(0.00))
                .andExpect(jsonPath("$.monthlyRevenue").value(0.00))
                .andExpect(jsonPath("$.semesterRevenue").value(0.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle very large revenue values")
    void shouldHandleVeryLargeRevenueValues() throws Exception {
        // Given
        RevenueStatisticsResponse largeStatistics = RevenueStatisticsResponse.of(
            new BigDecimal("999999999.99"),
            new BigDecimal("1000000.00"),
            new BigDecimal("50000000.00"),
            new BigDecimal("300000000.00")
        );
        
        when(statisticsService.getRevenueStatistics()).thenReturn(largeStatistics);

        // When & Then
        mockMvc.perform(get("/statistics/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(999999999.99))
                .andExpect(jsonPath("$.dailyRevenue").value(1000000.00))
                .andExpect(jsonPath("$.monthlyRevenue").value(50000000.00))
                .andExpect(jsonPath("$.semesterRevenue").value(300000000.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle fractional revenue values")
    void shouldHandleFractionalRevenueValues() throws Exception {
        // Given
        RevenueStatisticsResponse fractionalStatistics = RevenueStatisticsResponse.of(
            new BigDecimal("12345.67"),
            new BigDecimal("123.45"),
            new BigDecimal("5432.10"),
            new BigDecimal("32456.78")
        );
        
        when(statisticsService.getRevenueStatistics()).thenReturn(fractionalStatistics);

        // When & Then
        mockMvc.perform(get("/statistics/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(12345.67))
                .andExpect(jsonPath("$.dailyRevenue").value(123.45))
                .andExpect(jsonPath("$.monthlyRevenue").value(5432.10))
                .andExpect(jsonPath("$.semesterRevenue").value(32456.78));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get statistics for different state IDs")
    void shouldGetStatisticsForDifferentStateIds() throws Exception {
        // Given
        RevenueStatisticsResponse state1Statistics = RevenueStatisticsResponse.of(
            new BigDecimal("10000.00"),
            new BigDecimal("300.00"),
            new BigDecimal("5000.00"),
            new BigDecimal("30000.00")
        );
        
        RevenueStatisticsResponse state2Statistics = RevenueStatisticsResponse.of(
            new BigDecimal("20000.00"),
            new BigDecimal("600.00"),
            new BigDecimal("10000.00"),
            new BigDecimal("60000.00")
        );
        
        when(statisticsService.getRevenueStatistics(1L)).thenReturn(state1Statistics);
        when(statisticsService.getRevenueStatistics(2L)).thenReturn(state2Statistics);

        // When & Then - Test state 1
        mockMvc.perform(get("/statistics/revenue/state/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(10000.00))
                .andExpect(jsonPath("$.dailyRevenue").value(300.00));

        // When & Then - Test state 2
        mockMvc.perform(get("/statistics/revenue/state/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(20000.00))
                .andExpect(jsonPath("$.dailyRevenue").value(600.00));
    }

    @Test
    @DisplayName("Should return 401 for unauthorized access to revenue statistics")
    void shouldReturn401ForUnauthorizedAccessToRevenueStatistics() throws Exception {
        mockMvc.perform(get("/statistics/revenue"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 for unauthorized access to state revenue statistics")
    void shouldReturn401ForUnauthorizedAccessToStateRevenueStatistics() throws Exception {
        mockMvc.perform(get("/statistics/revenue/state/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden access to revenue statistics")
    void shouldReturn403ForForbiddenAccessToRevenueStatistics() throws Exception {
        mockMvc.perform(get("/statistics/revenue"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden access to state revenue statistics")
    void shouldReturn403ForForbiddenAccessToStateRevenueStatistics() throws Exception {
        mockMvc.perform(get("/statistics/revenue/state/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle invalid state ID format")
    void shouldHandleInvalidStateIdFormat() throws Exception {
        mockMvc.perform(get("/statistics/revenue/state/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle negative state IDs")
    void shouldHandleNegativeStateIds() throws Exception {
        // Given
        RevenueStatisticsResponse negativeStateStats = RevenueStatisticsResponse.of(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
        
        when(statisticsService.getRevenueStatistics(-1L)).thenReturn(negativeStateStats);

        // When & Then
        mockMvc.perform(get("/statistics/revenue/state/-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(0.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle very large state IDs")
    void shouldHandleVeryLargeStateIds() throws Exception {
        // Given
        Long largeStateId = 999999999999L;
        RevenueStatisticsResponse largeStateStats = RevenueStatisticsResponse.of(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
        
        when(statisticsService.getRevenueStatistics(largeStateId)).thenReturn(largeStateStats);

        // When & Then
        mockMvc.perform(get("/statistics/revenue/state/" + largeStateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(0.00));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Should allow manager role access to statistics")
    void shouldAllowManagerRoleAccessToStatistics() throws Exception {
        // Given
        when(statisticsService.getRevenueStatistics()).thenReturn(statisticsResponse);

        // When & Then
        mockMvc.perform(get("/statistics/revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service layer exceptions gracefully")
    void shouldHandleServiceLayerExceptionsGracefully() throws Exception {
        // Given
        when(statisticsService.getRevenueStatistics())
            .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/statistics/revenue"))
                .andExpect(status().isInternalServerError());
    }
}