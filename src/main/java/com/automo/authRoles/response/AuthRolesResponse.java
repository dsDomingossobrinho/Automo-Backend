package com.automo.authRoles.response;

import java.time.LocalDateTime;

public record AuthRolesResponse(
    Long id,
    Long authId,
    String authEmail,
    String authUsername,
    Long roleId,
    String roleName,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 