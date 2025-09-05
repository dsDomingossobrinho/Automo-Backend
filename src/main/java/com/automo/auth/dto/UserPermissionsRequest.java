package com.automo.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserPermissionsRequest(
    @NotNull(message = "ID do usuário é obrigatório")
    Long userId,
    
    @NotEmpty(message = "Lista de roles não pode estar vazia")
    List<Long> roleIds
) {}