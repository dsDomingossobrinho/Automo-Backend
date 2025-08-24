package com.automo.deal.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DealResponse(
    Long id,
    Long identifierId,
    Long leadId,
    String leadName,
    Long promotionId,
    String promotionName,
    BigDecimal total,
    LocalDate deliveryDate,
    Integer messageCount,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 