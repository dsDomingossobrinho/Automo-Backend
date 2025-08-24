package com.automo.agentAreas.response;

import java.time.LocalDateTime;

public record AgentAreasResponse(
    Long id,
    Long agentId,
    String agentName,
    Long areaId,
    String areaName,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 