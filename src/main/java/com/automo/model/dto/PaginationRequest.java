package com.automo.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO base para requisições paginadas com busca
 */
public record PaginationRequest(
    
    /**
     * Campo para busca (opcional)
     */
    String search,
    
    /**
     * Número da página (começa em 0)
     */
    @NotNull(message = "Número da página é obrigatório")
    @Min(value = 0, message = "Número da página deve ser maior ou igual a 0")
    Integer page,
    
    /**
     * Tamanho da página
     */
    @NotNull(message = "Tamanho da página é obrigatório")
    @Min(value = 1, message = "Tamanho da página deve ser maior que 0")
    Integer size,
    
    /**
     * Campo para ordenação (opcional)
     */
    String sortBy,
    
    /**
     * Direção da ordenação: ASC ou DESC (padrão: ASC)
     */
    String sortDirection
) {
    
    /**
     * Construtor com valores padrão
     */
    public PaginationRequest {
        if (search == null) search = "";
        if (page == null) page = 0;
        if (size == null) size = 10;
        if (sortBy == null) sortBy = "id";
        if (sortDirection == null) sortDirection = "ASC";
    }
    
    /**
     * Construtor simplificado
     */
    public PaginationRequest(String search, Integer page, Integer size) {
        this(search, page, size, "id", "ASC");
    }
    
    /**
     * Construtor apenas com paginação
     */
    public PaginationRequest(Integer page, Integer size) {
        this("", page, size, "id", "ASC");
    }
}
