package com.automo.paymentType.repository;

import com.automo.paymentType.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {

    Optional<PaymentType> findByType(String type);
    
    boolean existsByType(String type);
} 