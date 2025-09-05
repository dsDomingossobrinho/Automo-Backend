package com.automo.statistics.service;

import com.automo.payment.repository.PaymentRepository;
import com.automo.state.service.StateService;
import com.automo.statistics.dto.RevenueStatisticsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final PaymentRepository paymentRepository;
    private final StateService stateService;

    @Override
    public RevenueStatisticsResponse getRevenueStatistics() {
        log.info("Calculating revenue statistics");
        
        // Estados considerados como "pagamentos aprovados/confirmados"
        // Pode ajustar conforme os IDs dos estados no seu sistema
        List<Long> approvedStateIds = getApprovedPaymentStateIds();
        
        try {
            // Calcular todas as estatísticas
            BigDecimal totalRevenue = paymentRepository.calculateTotalRevenue(approvedStateIds);
            BigDecimal dailyRevenue = paymentRepository.calculateDailyRevenue(approvedStateIds);
            BigDecimal monthlyRevenue = paymentRepository.calculateMonthlyRevenue(approvedStateIds);
            BigDecimal semesterRevenue = calculateCurrentSemesterRevenue(approvedStateIds);
            
            log.info("Revenue statistics calculated - Total: {}, Daily: {}, Monthly: {}, Semester: {}", 
                    totalRevenue, dailyRevenue, monthlyRevenue, semesterRevenue);
            
            return RevenueStatisticsResponse.of(
                totalRevenue,
                dailyRevenue,
                monthlyRevenue,
                semesterRevenue
            );
            
        } catch (Exception e) {
            log.error("Error calculating revenue statistics", e);
            // Retornar estatísticas zeradas em caso de erro
            return RevenueStatisticsResponse.of(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
            );
        }
    }

    @Override
    public RevenueStatisticsResponse getRevenueStatistics(Long stateId) {
        log.info("Calculating revenue statistics for state: {}", stateId);
        
        // Filtrar apenas pelo estado específico
        List<Long> specificStateIds = Arrays.asList(stateId);
        
        try {
            BigDecimal totalRevenue = paymentRepository.calculateTotalRevenue(specificStateIds);
            BigDecimal dailyRevenue = paymentRepository.calculateDailyRevenue(specificStateIds);
            BigDecimal monthlyRevenue = paymentRepository.calculateMonthlyRevenue(specificStateIds);
            BigDecimal semesterRevenue = calculateCurrentSemesterRevenue(specificStateIds);
            
            log.info("Revenue statistics for state {} calculated - Total: {}, Daily: {}, Monthly: {}, Semester: {}", 
                    stateId, totalRevenue, dailyRevenue, monthlyRevenue, semesterRevenue);
            
            return RevenueStatisticsResponse.of(
                totalRevenue,
                dailyRevenue,
                monthlyRevenue,
                semesterRevenue
            );
            
        } catch (Exception e) {
            log.error("Error calculating revenue statistics for state: {}", stateId, e);
            return RevenueStatisticsResponse.of(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
            );
        }
    }

    /**
     * Calcula a receita do semestre atual
     */
    private BigDecimal calculateCurrentSemesterRevenue(List<Long> approvedStateIds) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime semesterStart;
        LocalDateTime semesterEnd;
        
        // Determinar se estamos no primeiro ou segundo semestre
        if (now.getMonth().getValue() <= 6) {
            // Primeiro semestre (Janeiro - Junho)
            semesterStart = LocalDateTime.of(now.getYear(), Month.JANUARY, 1, 0, 0);
            semesterEnd = LocalDateTime.of(now.getYear(), Month.JUNE, 30, 23, 59, 59);
        } else {
            // Segundo semestre (Julho - Dezembro)
            semesterStart = LocalDateTime.of(now.getYear(), Month.JULY, 1, 0, 0);
            semesterEnd = LocalDateTime.of(now.getYear(), Month.DECEMBER, 31, 23, 59, 59);
        }
        
        return paymentRepository.calculateSemesterRevenue(approvedStateIds, semesterStart, semesterEnd);
    }

    /**
     * Obtém os IDs dos estados considerados como pagamentos aprovados
     * Ajuste conforme os estados disponíveis no seu sistema
     */
    private List<Long> getApprovedPaymentStateIds() {
        // IDs dos estados que representam pagamentos aprovados/confirmados
        // Exemplo: 1 = ACTIVE, 5 = APPROVED_PAYMENT
        // Ajuste conforme os IDs reais dos estados no seu sistema
        return Arrays.asList(1L, 5L); // Assumindo ACTIVE=1 e APPROVED_PAYMENT=5
    }
}