package com.automo.deal.repository;

import com.automo.deal.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    
    List<Deal> findByStateId(Long stateId);
    List<Deal> findByIdentifierId(Long identifierId);
    List<Deal> findByLeadId(Long leadId);
    List<Deal> findByPromotionId(Long promotionId);
} 