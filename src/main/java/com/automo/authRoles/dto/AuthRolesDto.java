package com.automo.authRoles.dto;

import jakarta.validation.constraints.NotNull;

public record AuthRolesDto(
    @NotNull(message = "Auth ID is required") Long authId,
    @NotNull(message = "Role ID is required") Long roleId,
    @NotNull(message = "State ID is required") Long stateId
) {} 