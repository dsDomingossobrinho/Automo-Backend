package com.automo.payment.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePaymentTypeRequest(
    @NotNull(message = "Payment Type ID is required")
    Long paymentTypeId
) {
}