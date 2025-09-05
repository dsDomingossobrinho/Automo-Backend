package com.automo.payment.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
    Long id,
    String document,
    Long identifierId,
    Long stateId,
    String stateName,
    Long paymentTypeId,
    String paymentTypeName,
    BigDecimal amount,
    String imageFilename,
    String originalFilename,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 