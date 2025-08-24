package com.automo.payment.repository;

import com.automo.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByStateId(Long stateId);
    List<Payment> findByPaymentTypeId(Long paymentTypeId);
    List<Payment> findByIdentifier(String identifier);
} 