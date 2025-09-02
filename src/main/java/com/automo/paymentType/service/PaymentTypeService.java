package com.automo.paymentType.service;

import com.automo.paymentType.dto.PaymentTypeDto;
import com.automo.paymentType.entity.PaymentType;
import com.automo.paymentType.response.PaymentTypeResponse;

import java.util.List;

public interface PaymentTypeService {

    PaymentTypeResponse createPaymentType(PaymentTypeDto paymentTypeDto);

    PaymentTypeResponse updatePaymentType(Long id, PaymentTypeDto paymentTypeDto);

    List<PaymentTypeResponse> getAllPaymentTypes();

    PaymentType getPaymentTypeById(Long id);

    PaymentTypeResponse getPaymentTypeByIdResponse(Long id);

    void deletePaymentType(Long id);
    
    /**
     * Busca PaymentType por ID - método obrigatório para comunicação entre services
     */
    PaymentType findById(Long id);
    
    /**
     * Busca PaymentType por ID e estado específico (state_id = 1 por padrão)
     */
    PaymentType findByIdAndStateId(Long id, Long stateId);
} 