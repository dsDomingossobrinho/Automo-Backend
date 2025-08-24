package com.automo.lead.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LeadDto(
    @NotNull(message = "ID do identificador é obrigatório")
    Long identifierId,
    
    @NotBlank(message = "Nome é obrigatório")
    String name,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email,
    
    String contact,
    
    String zone,
    
    @NotNull(message = "ID do tipo de lead é obrigatório")
    Long leadTypeId,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 