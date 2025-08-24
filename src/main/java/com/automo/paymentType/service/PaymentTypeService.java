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
} 