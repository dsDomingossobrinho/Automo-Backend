package com.automo.payment.service;

import com.automo.payment.dto.PaymentDto;
import com.automo.payment.entity.Payment;
import com.automo.payment.response.PaymentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentDto paymentDto);

    PaymentResponse updatePayment(Long id, PaymentDto paymentDto);

    List<PaymentResponse> getAllPayments();

    Payment getPaymentById(Long id);

    PaymentResponse getPaymentByIdResponse(Long id);

    List<PaymentResponse> getPaymentsByState(Long stateId);

    List<PaymentResponse> getPaymentsByType(Long paymentTypeId);

    List<PaymentResponse> getPaymentsByIdentifier(Long identifierId);

    void deletePayment(Long id);
    
    /**
     * Busca Payment por ID - método obrigatório para comunicação entre services
     */
    Payment findById(Long id);
    
    /**
     * Busca Payment por ID e estado específico (state_id = 1 por padrão)
     */
    Payment findByIdAndStateId(Long id, Long stateId);
    
    /**
     * Cria pagamento com ficheiro
     */
    PaymentResponse createPaymentWithFile(Long identifierId, Long stateId, Long paymentTypeId, java.math.BigDecimal amount, MultipartFile file);
    
    /**
     * Atualiza estado do pagamento
     */
    PaymentResponse updatePaymentState(Long paymentId, Long stateId);
    
    /**
     * Atualiza tipo de pagamento
     */
    PaymentResponse updatePaymentType(Long paymentId, Long paymentTypeId);
    
    /**
     * Atualiza ficheiro do pagamento
     */
    PaymentResponse updatePaymentFile(Long paymentId, MultipartFile file);
} 