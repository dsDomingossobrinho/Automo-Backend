package com.automo.payment.repository;

import com.automo.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByStateId(Long stateId);
    List<Payment> findByPaymentTypeId(Long paymentTypeId);
    List<Payment> findByIdentifierId(Long identifierId);
    
    // ==========================================
    // MÉTODOS PARA ESTATÍSTICAS DE FATURAÇÃO
    // ==========================================
    
    /**
     * Calcula o total faturado geral (todos os pagamentos aprovados)
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.state.id IN :approvedStateIds")
    BigDecimal calculateTotalRevenue(@Param("approvedStateIds") List<Long> approvedStateIds);
    
    /**
     * Calcula o total faturado no dia atual
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.state.id IN :approvedStateIds " +
           "AND FUNCTION('DATE', p.createdAt) = CURRENT_DATE")
    BigDecimal calculateDailyRevenue(@Param("approvedStateIds") List<Long> approvedStateIds);
    
    /**
     * Calcula o total faturado no mês atual
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.state.id IN :approvedStateIds " +
           "AND FUNCTION('YEAR', p.createdAt) = FUNCTION('YEAR', CURRENT_DATE) " +
           "AND FUNCTION('MONTH', p.createdAt) = FUNCTION('MONTH', CURRENT_DATE)")
    BigDecimal calculateMonthlyRevenue(@Param("approvedStateIds") List<Long> approvedStateIds);
    
    /**
     * Calcula o total faturado no semestre atual
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.state.id IN :approvedStateIds " +
           "AND p.createdAt >= :semesterStart " +
           "AND p.createdAt <= :semesterEnd")
    BigDecimal calculateSemesterRevenue(
        @Param("approvedStateIds") List<Long> approvedStateIds,
        @Param("semesterStart") LocalDateTime semesterStart,
        @Param("semesterEnd") LocalDateTime semesterEnd
    );
    
    /**
     * Calcula o total faturado entre duas datas
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.state.id IN :approvedStateIds " +
           "AND p.createdAt >= :startDate " +
           "AND p.createdAt <= :endDate")
    BigDecimal calculateRevenueByDateRange(
        @Param("approvedStateIds") List<Long> approvedStateIds,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
} 