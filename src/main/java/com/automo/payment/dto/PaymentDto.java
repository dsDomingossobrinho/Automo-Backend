package com.automo.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentDto(
    @NotBlank(message = "Documento é obrigatório")
    String document,
    
    @NotBlank(message = "Identificador é obrigatório")
    String identifier,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId,
    
    @NotNull(message = "ID do tipo de pagamento é obrigatório")
    Long paymentTypeId
) {} 