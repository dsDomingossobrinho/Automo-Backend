package com.automo.payment.response;

import java.time.LocalDateTime;

public record PaymentResponse(
    Long id,
    String document,
    String identifier,
    Long stateId,
    String stateName,
    Long paymentTypeId,
    String paymentTypeName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 