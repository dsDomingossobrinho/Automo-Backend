package com.automo.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record CreatePaymentWithFileRequest(
    @NotNull(message = "Identifier ID is required")
    Long identifierId,
    
    @NotNull(message = "State ID is required")
    Long stateId,
    
    @NotNull(message = "Payment Type ID is required")
    Long paymentTypeId,
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    BigDecimal amount
) {
}