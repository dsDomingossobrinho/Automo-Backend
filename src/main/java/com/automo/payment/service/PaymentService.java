package com.automo.payment.service;

import com.automo.payment.dto.PaymentDto;
import com.automo.payment.entity.Payment;
import com.automo.payment.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentDto paymentDto);

    PaymentResponse updatePayment(Long id, PaymentDto paymentDto);

    List<PaymentResponse> getAllPayments();

    Payment getPaymentById(Long id);

    PaymentResponse getPaymentByIdResponse(Long id);

    List<PaymentResponse> getPaymentsByState(Long stateId);

    List<PaymentResponse> getPaymentsByType(Long paymentTypeId);

    List<PaymentResponse> getPaymentsByIdentifier(String identifier);

    void deletePayment(Long id);
    
    /**
     * Busca Payment por ID - método obrigatório para comunicação entre services
     */
    Payment findById(Long id);
    
    /**
     * Busca Payment por ID e estado específico (state_id = 1 por padrão)
     */
    Payment findByIdAndStateId(Long id, Long stateId);
} 