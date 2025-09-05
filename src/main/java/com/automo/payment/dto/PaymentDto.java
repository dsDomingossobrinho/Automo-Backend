package com.automo.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record PaymentDto(
    @NotBlank(message = "Documento é obrigatório")
    String document,
    
    @NotNull(message = "ID do identificador é obrigatório")
    Long identifierId,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId,
    
    @NotNull(message = "ID do tipo de pagamento é obrigatório")
    Long paymentTypeId,
    
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser maior que zero")
    BigDecimal amount
) {} 