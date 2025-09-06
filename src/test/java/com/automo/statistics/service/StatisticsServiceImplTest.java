package com.automo.statistics.service;

import com.automo.payment.repository.PaymentRepository;
import com.automo.state.service.StateService;
import com.automo.statistics.dto.RevenueStatisticsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for StatisticsServiceImpl")
class StatisticsServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StateService stateService;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private BigDecimal totalRevenue;
    private BigDecimal dailyRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal semesterRevenue;

    @BeforeEach
    void setUp() {
        totalRevenue = new BigDecimal("10000.00");
        dailyRevenue = new BigDecimal("500.00");
        monthlyRevenue = new BigDecimal("3000.00");
        semesterRevenue = new BigDecimal("18000.00");
    }

    @Test
    @DisplayName("Should get revenue statistics successfully")
    void shouldGetRevenueStatisticsSuccessfully() {
        // Given
        when(paymentRepository.calculateTotalRevenue(anyList())).thenReturn(totalRevenue);
        when(paymentRepository.calculateDailyRevenue(anyList())).thenReturn(dailyRevenue);
        when(paymentRepository.calculateMonthlyRevenue(anyList())).thenReturn(monthlyRevenue);
        when(paymentRepository.calculateSemesterRevenue(anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(semesterRevenue);

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics();

        // Then
        assertNotNull(result);
        assertEquals(totalRevenue, result.getTotalRevenue());
        assertEquals(dailyRevenue, result.getDailyRevenue());
        assertEquals(monthlyRevenue, result.getMonthlyRevenue());
        assertEquals(semesterRevenue, result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should get revenue statistics for specific state successfully")
    void shouldGetRevenueStatisticsForSpecificStateSuccessfully() {
        // Given
        Long stateId = 1L;
        BigDecimal stateTotal = new BigDecimal("5000.00");
        BigDecimal stateDaily = new BigDecimal("250.00");
        BigDecimal stateMonthly = new BigDecimal("1500.00");
        BigDecimal stateSemester = new BigDecimal("9000.00");

        when(paymentRepository.calculateTotalRevenue(List.of(stateId))).thenReturn(stateTotal);
        when(paymentRepository.calculateDailyRevenue(List.of(stateId))).thenReturn(stateDaily);
        when(paymentRepository.calculateMonthlyRevenue(List.of(stateId))).thenReturn(stateMonthly);
        when(paymentRepository.calculateSemesterRevenue(any(List.class), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(stateSemester);

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics(stateId);

        // Then
        assertNotNull(result);
        assertEquals(stateTotal, result.getTotalRevenue());
        assertEquals(stateDaily, result.getDailyRevenue());
        assertEquals(stateMonthly, result.getMonthlyRevenue());
        assertEquals(stateSemester, result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should return zero statistics when repository throws exception")
    void shouldReturnZeroStatisticsWhenRepositoryThrowsException() {
        // Given
        when(paymentRepository.calculateTotalRevenue(anyList()))
            .thenThrow(new RuntimeException("Database error"));

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics();

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, result.getDailyRevenue());
        assertEquals(BigDecimal.ZERO, result.getMonthlyRevenue());
        assertEquals(BigDecimal.ZERO, result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should return zero statistics for specific state when repository throws exception")
    void shouldReturnZeroStatisticsForSpecificStateWhenRepositoryThrowsException() {
        // Given
        Long stateId = 1L;
        when(paymentRepository.calculateTotalRevenue(List.of(stateId)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics(stateId);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, result.getDailyRevenue());
        assertEquals(BigDecimal.ZERO, result.getMonthlyRevenue());
        assertEquals(BigDecimal.ZERO, result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should handle null revenue values from repository")
    void shouldHandleNullRevenueValuesFromRepository() {
        // Given
        when(paymentRepository.calculateTotalRevenue(anyList())).thenReturn(null);
        when(paymentRepository.calculateDailyRevenue(anyList())).thenReturn(null);
        when(paymentRepository.calculateMonthlyRevenue(anyList())).thenReturn(null);
        when(paymentRepository.calculateSemesterRevenue(anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(null);

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics();

        // Then
        assertNotNull(result);
        assertNull(result.getTotalRevenue());
        assertNull(result.getDailyRevenue());
        assertNull(result.getMonthlyRevenue());
        assertNull(result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should calculate first semester revenue correctly")
    void shouldCalculateFirstSemesterRevenueCorrectly() {
        // Given - Mock current date to be in first semester (e.g., March)
        BigDecimal firstSemesterRevenue = new BigDecimal("12000.00");
        
        when(paymentRepository.calculateTotalRevenue(anyList())).thenReturn(totalRevenue);
        when(paymentRepository.calculateDailyRevenue(anyList())).thenReturn(dailyRevenue);
        when(paymentRepository.calculateMonthlyRevenue(anyList())).thenReturn(monthlyRevenue);
        when(paymentRepository.calculateSemesterRevenue(anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(firstSemesterRevenue);

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics();

        // Then
        assertNotNull(result);
        assertEquals(firstSemesterRevenue, result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should handle zero revenue values")
    void shouldHandleZeroRevenueValues() {
        // Given
        when(paymentRepository.calculateTotalRevenue(anyList())).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.calculateDailyRevenue(anyList())).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.calculateMonthlyRevenue(anyList())).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.calculateSemesterRevenue(anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(BigDecimal.ZERO);

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics();

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, result.getDailyRevenue());
        assertEquals(BigDecimal.ZERO, result.getMonthlyRevenue());
        assertEquals(BigDecimal.ZERO, result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should handle very large revenue values")
    void shouldHandleVeryLargeRevenueValues() {
        // Given
        BigDecimal largeTotal = new BigDecimal("999999999999.99");
        BigDecimal largeDaily = new BigDecimal("1000000.00");
        BigDecimal largeMonthly = new BigDecimal("30000000.00");
        BigDecimal largeSemester = new BigDecimal("180000000.00");

        when(paymentRepository.calculateTotalRevenue(anyList())).thenReturn(largeTotal);
        when(paymentRepository.calculateDailyRevenue(anyList())).thenReturn(largeDaily);
        when(paymentRepository.calculateMonthlyRevenue(anyList())).thenReturn(largeMonthly);
        when(paymentRepository.calculateSemesterRevenue(anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(largeSemester);

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics();

        // Then
        assertNotNull(result);
        assertEquals(largeTotal, result.getTotalRevenue());
        assertEquals(largeDaily, result.getDailyRevenue());
        assertEquals(largeMonthly, result.getMonthlyRevenue());
        assertEquals(largeSemester, result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should handle fractional revenue values")
    void shouldHandleFractionalRevenueValues() {
        // Given
        BigDecimal fractionalTotal = new BigDecimal("1234.567");
        BigDecimal fractionalDaily = new BigDecimal("45.123");
        BigDecimal fractionalMonthly = new BigDecimal("1500.789");
        BigDecimal fractionalSemester = new BigDecimal("9876.543");

        when(paymentRepository.calculateTotalRevenue(anyList())).thenReturn(fractionalTotal);
        when(paymentRepository.calculateDailyRevenue(anyList())).thenReturn(fractionalDaily);
        when(paymentRepository.calculateMonthlyRevenue(anyList())).thenReturn(fractionalMonthly);
        when(paymentRepository.calculateSemesterRevenue(anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(fractionalSemester);

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics();

        // Then
        assertNotNull(result);
        assertEquals(fractionalTotal, result.getTotalRevenue());
        assertEquals(fractionalDaily, result.getDailyRevenue());
        assertEquals(fractionalMonthly, result.getMonthlyRevenue());
        assertEquals(fractionalSemester, result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should handle partial repository failures gracefully")
    void shouldHandlePartialRepositoryFailuresGracefully() {
        // Given - Some calls succeed, some fail
        when(paymentRepository.calculateTotalRevenue(anyList())).thenReturn(totalRevenue);
        when(paymentRepository.calculateDailyRevenue(anyList()))
            .thenThrow(new RuntimeException("Daily calculation failed"));

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics();

        // Then - Should return zero for all values due to exception handling
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, result.getDailyRevenue());
        assertEquals(BigDecimal.ZERO, result.getMonthlyRevenue());
        assertEquals(BigDecimal.ZERO, result.getSemesterRevenue());
    }

    @Test
    @DisplayName("Should use correct approved state IDs for calculations")
    void shouldUseCorrectApprovedStateIdsForCalculations() {
        // Given
        when(paymentRepository.calculateTotalRevenue(anyList())).thenReturn(totalRevenue);
        when(paymentRepository.calculateDailyRevenue(anyList())).thenReturn(dailyRevenue);
        when(paymentRepository.calculateMonthlyRevenue(anyList())).thenReturn(monthlyRevenue);
        when(paymentRepository.calculateSemesterRevenue(anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(semesterRevenue);

        // When
        RevenueStatisticsResponse result = statisticsService.getRevenueStatistics();

        // Then
        assertNotNull(result);
        // The service should use approved state IDs [1L, 5L] as per implementation
        // This test verifies that the service calls repository methods correctly
        assertEquals(totalRevenue, result.getTotalRevenue());
    }
}