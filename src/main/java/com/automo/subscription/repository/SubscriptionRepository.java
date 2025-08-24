package com.automo.subscription.repository;

import com.automo.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    List<Subscription> findByStateId(Long stateId);
    List<Subscription> findByUserId(Long userId);
    List<Subscription> findByUserIdAndStateId(Long userId, Long stateId);
    List<Subscription> findByPlanId(Long planId);
    List<Subscription> findByPromotionId(Long promotionId);
    List<Subscription> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<Subscription> findByEndDateBefore(LocalDate date);
} 