package com.automo.agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgentDto(
    @NotBlank(message = "Nome é obrigatório")
    String name,
    
    String description,
    
    String location,
    
    String restrictions,
    
    String activityFlow,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 