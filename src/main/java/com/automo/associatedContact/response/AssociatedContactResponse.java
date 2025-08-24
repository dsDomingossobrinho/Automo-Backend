package com.automo.associatedContact.response;

import java.time.LocalDateTime;

public record AssociatedContactResponse(
    Long id,
    Long identifierId,
    String identifierName,
    String contact,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 