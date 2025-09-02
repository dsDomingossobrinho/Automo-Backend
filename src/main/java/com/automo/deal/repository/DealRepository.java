package com.automo.deal.repository;

import com.automo.deal.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    
    /**
     * Find all deals with all related data eagerly loaded
     */
    @Query("SELECT d FROM Deal d " +
           "LEFT JOIN FETCH d.identifier " +
           "LEFT JOIN FETCH d.lead " +
           "LEFT JOIN FETCH d.promotion " +
           "LEFT JOIN FETCH d.state")
    List<Deal> findAllWithRelations();
    
    /**
     * Find deal by ID with all related data eagerly loaded
     */
    @Query("SELECT d FROM Deal d " +
           "LEFT JOIN FETCH d.identifier " +
           "LEFT JOIN FETCH d.lead " +
           "LEFT JOIN FETCH d.promotion " +
           "LEFT JOIN FETCH d.state " +
           "WHERE d.id = :id")
    Optional<Deal> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Find deals by state ID with all related data eagerly loaded
     */
    @Query("SELECT d FROM Deal d " +
           "LEFT JOIN FETCH d.identifier " +
           "LEFT JOIN FETCH d.lead " +
           "LEFT JOIN FETCH d.promotion " +
           "LEFT JOIN FETCH d.state " +
           "WHERE d.state.id = :stateId")
    List<Deal> findByStateIdWithRelations(@Param("stateId") Long stateId);
    
    /**
     * Find deals by identifier ID with all related data eagerly loaded
     */
    @Query("SELECT d FROM Deal d " +
           "LEFT JOIN FETCH d.identifier " +
           "LEFT JOIN FETCH d.lead " +
           "LEFT JOIN FETCH d.promotion " +
           "LEFT JOIN FETCH d.state " +
           "WHERE d.identifier.id = :identifierId")
    List<Deal> findByIdentifierIdWithRelations(@Param("identifierId") Long identifierId);
    
    /**
     * Find deals by lead ID with all related data eagerly loaded
     */
    @Query("SELECT d FROM Deal d " +
           "LEFT JOIN FETCH d.identifier " +
           "LEFT JOIN FETCH d.lead " +
           "LEFT JOIN FETCH d.promotion " +
           "LEFT JOIN FETCH d.state " +
           "WHERE d.lead.id = :leadId")
    List<Deal> findByLeadIdWithRelations(@Param("leadId") Long leadId);
    
    /**
     * Find deals by promotion ID with all related data eagerly loaded
     */
    @Query("SELECT d FROM Deal d " +
           "LEFT JOIN FETCH d.identifier " +
           "LEFT JOIN FETCH d.lead " +
           "LEFT JOIN FETCH d.promotion " +
           "LEFT JOIN FETCH d.state " +
           "WHERE d.promotion.id = :promotionId")
    List<Deal> findByPromotionIdWithRelations(@Param("promotionId") Long promotionId);
    
    // Keep original methods for backward compatibility
    List<Deal> findByStateId(Long stateId);
    List<Deal> findByIdentifierId(Long identifierId);
    List<Deal> findByLeadId(Long leadId);
    List<Deal> findByPromotionId(Long promotionId);
} 