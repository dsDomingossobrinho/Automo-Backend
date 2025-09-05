package com.automo.payment.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStateRequest(
    @NotNull(message = "State ID is required")
    Long stateId
) {
}