package com.automo.model.service;

import com.automo.model.dto.PaginationRequest;
import com.automo.model.dto.PaginatedResponse;

import java.util.List;

/**
 * Interface base para services com funcionalidades comuns
 * @param <T> Tipo da entidade
 * @param <R> Tipo da resposta DTO
 * @param <ID> Tipo do ID da entidade
 */
public interface BaseService<T, R, ID> {

    /**
     * Busca entidades com paginação e busca
     */
    PaginatedResponse<R> getEntitiesPaginated(PaginationRequest request);

    /**
     * Busca entidades ativas com paginação e busca
     */
    PaginatedResponse<R> getActiveEntitiesPaginated(PaginationRequest request);

    /**
     * Busca entidades por estado com paginação e busca
     */
    PaginatedResponse<R> getEntitiesByStatePaginated(Long stateId, PaginationRequest request);

    /**
     * Busca todas as entidades ativas
     */
    List<R> getAllActiveEntities();

    /**
     * Busca entidade por ID (verifica estado ativo)
     */
    T getActiveEntityById(ID id);

    /**
     * Busca entidade por ID com resposta DTO (verifica estado ativo)
     */
    R getActiveEntityByIdResponse(ID id);

    /**
     * Busca entidades por estado específico
     */
    List<R> getEntitiesByState(Long stateId);

    /**
     * Desativa uma entidade (soft delete)
     */
    void deactivateEntity(ID id);

    /**
     * Busca entidade por ID sem verificar estado (para uso interno)
     */
    T getEntityById(ID id);

    /**
     * Verifica se entidade existe e está ativa
     */
    boolean isEntityActive(ID id);

    /**
     * Conta total de entidades ativas
     */
    long countActiveEntities();

    /**
     * Conta total de entidades por estado
     */
    long countEntitiesByState(Long stateId);
}
