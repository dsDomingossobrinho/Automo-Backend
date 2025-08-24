package com.automo.identifier.response;

import java.time.LocalDateTime;

public record IdentifierResponse(
    Long id,
    Long userId,
    String userName,
    Long identifierTypeId,
    String identifierTypeName,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 