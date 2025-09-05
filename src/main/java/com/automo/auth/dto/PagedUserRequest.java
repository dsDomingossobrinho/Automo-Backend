package com.automo.auth.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record PagedUserRequest(
    @Min(value = 0, message = "Página deve ser maior ou igual a 0")
    Integer page,
    
    @Min(value = 1, message = "Tamanho da página deve ser maior que 0")
    Integer size,
    
    @Size(max = 255, message = "Termo de pesquisa deve ter no máximo 255 caracteres")
    String search,
    
    String sortBy,
    
    String sortDirection
) {
    public PagedUserRequest {
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null) sortBy = "createdAt";
        if (sortDirection == null) sortDirection = "DESC";
    }
}