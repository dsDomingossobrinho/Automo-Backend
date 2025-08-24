package com.automo.subscriptionPlan.repository;

import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    
    List<SubscriptionPlan> findByStateId(Long stateId);
} 