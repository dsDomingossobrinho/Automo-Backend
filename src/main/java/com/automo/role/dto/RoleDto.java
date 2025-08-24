package com.automo.role.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleDto(
    @NotBlank(message = "Role is required")
    String role,
    
    String description
) {} 