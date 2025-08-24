package com.automo.associatedEmail.response;

import java.time.LocalDateTime;

public record AssociatedEmailResponse(
    Long id,
    Long identifierId,
    String identifierName,
    String email,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 