package com.automo.model.dto;

import java.util.List;

/**
 * DTO de resposta paginada genérica
 * @param <T> Tipo dos dados retornados
 */
public record PaginatedResponse<T>(
    
    /**
     * Lista de dados da página atual
     */
    List<T> content,
    
    /**
     * Número da página atual
     */
    int pageNumber,
    
    /**
     * Tamanho da página
     */
    int pageSize,
    
    /**
     * Número total de elementos
     */
    long totalElements,
    
    /**
     * Número total de páginas
     */
    int totalPages,
    
    /**
     * Se é a primeira página
     */
    boolean first,
    
    /**
     * Se é a última página
     */
    boolean last,
    
    /**
     * Se tem próxima página
     */
    boolean hasNext,
    
    /**
     * Se tem página anterior
     */
    boolean hasPrevious
) {
    
    /**
     * Construtor com valores calculados automaticamente
     */
    public PaginatedResponse(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this(
            content,
            pageNumber,
            pageSize,
            totalElements,
            (int) Math.ceil((double) totalElements / pageSize),
            pageNumber == 0,
            pageNumber >= (int) Math.ceil((double) totalElements / pageSize) - 1,
            pageNumber < (int) Math.ceil((double) totalElements / pageSize) - 1,
            pageNumber > 0
        );
    }
    
    /**
     * Construtor vazio para casos especiais
     */
    public static <T> PaginatedResponse<T> empty(int pageNumber, int pageSize) {
        return new PaginatedResponse<>(
            List.of(),
            pageNumber,
            pageSize,
            0,
            0,
            true,
            true,
            false,
            false
        );
    }
}
