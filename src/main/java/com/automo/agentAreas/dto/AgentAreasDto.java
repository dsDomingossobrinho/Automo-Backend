package com.automo.agentAreas.dto;

import jakarta.validation.constraints.NotNull;

public record AgentAreasDto(
    @NotNull(message = "Agent ID is required")
    Long agentId,
    
    @NotNull(message = "Area ID is required")
    Long areaId,
    
    @NotNull(message = "State ID is required")
    Long stateId
) {} 