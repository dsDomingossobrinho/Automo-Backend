package com.automo.auth.dto;

import jakarta.validation.constraints.NotNull;

public record ManageRoleRequest(
    @NotNull(message = "ID do usuário é obrigatório")
    Long userId,
    
    @NotNull(message = "ID da role é obrigatório")
    Long roleId
) {}