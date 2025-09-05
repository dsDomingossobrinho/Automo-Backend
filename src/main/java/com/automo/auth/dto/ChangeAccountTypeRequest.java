package com.automo.auth.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeAccountTypeRequest(
    @NotNull(message = "ID do usuário é obrigatório")
    Long userId,
    
    @NotNull(message = "ID do tipo de conta é obrigatório")
    Long accountTypeId
) {}