package com.automo.paymentType.response;

import java.time.LocalDateTime;

public record PaymentTypeResponse(
    Long id,
    String type,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 