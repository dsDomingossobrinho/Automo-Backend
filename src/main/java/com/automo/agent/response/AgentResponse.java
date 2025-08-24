package com.automo.agent.response;

import java.time.LocalDateTime;

public record AgentResponse(
    Long id,
    String name,
    String description,
    String location,
    String restrictions,
    String activityFlow,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 