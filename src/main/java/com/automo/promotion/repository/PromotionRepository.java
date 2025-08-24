package com.automo.promotion.repository;

import com.automo.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    List<Promotion> findByStateId(Long stateId);
    Optional<Promotion> findByCode(String code);
} 