package com.automo.model.service;

import com.automo.model.dto.PaginationRequest;
import com.automo.model.dto.PaginatedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Implementação base abstrata para services com funcionalidades comuns
 * @param <T> Tipo da entidade
 * @param <R> Tipo da resposta DTO
 * @param <ID> Tipo do ID da entidade
 */
@Slf4j
public abstract class BaseServiceImpl<T, R, ID> implements BaseService<T, R, ID> {

    protected final JpaRepository<T, ID> repository;

    protected BaseServiceImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public PaginatedResponse<R> getEntitiesPaginated(PaginationRequest request) {
        log.info("Buscando entidades com paginação: page={}, size={}, search='{}'", 
                request.page(), request.size(), request.search());
        
        Pageable pageable = createPageable(request);
        Page<T> entityPage = getEntitiesPage(request, pageable);
        
        return createPaginatedResponse(entityPage, request);
    }

    @Override
    public PaginatedResponse<R> getActiveEntitiesPaginated(PaginationRequest request) {
        log.info("Buscando entidades ativas com paginação: page={}, size={}, search='{}'", 
                request.page(), request.size(), request.search());
        
        Pageable pageable = createPageable(request);
        Page<T> entityPage = getActiveEntitiesPage(request, pageable);
        
        return createPaginatedResponse(entityPage, request);
    }

    @Override
    public PaginatedResponse<R> getEntitiesByStatePaginated(Long stateId, PaginationRequest request) {
        log.info("Buscando entidades por estado {} com paginação: page={}, size={}, search='{}'", 
                stateId, request.page(), request.size(), request.search());
        
        validateStateExists(stateId);
        
        Pageable pageable = createPageable(request);
        Page<T> entityPage = getEntitiesByStatePage(stateId, request, pageable);
        
        return createPaginatedResponse(entityPage, request);
    }

    @Override
    public List<R> getAllActiveEntities() {
        log.info("Buscando todas as entidades ativas");
        return getActiveEntitiesList()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public T getActiveEntityById(ID id) {
        log.info("Buscando entidade ativa por ID: {}", id);
        T entity = getEntityById(id);
        
        if (!isEntityActive(id)) {
            throw new RuntimeException("Entity with ID " + id + " is not active");
        }
        
        return entity;
    }

    @Override
    public R getActiveEntityByIdResponse(ID id) {
        return mapToResponse(getActiveEntityById(id));
    }

    @Override
    public List<R> getEntitiesByState(Long stateId) {
        log.info("Buscando entidades por estado: {}", stateId);
        validateStateExists(stateId);
        
        return getEntitiesByStateList(stateId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deactivateEntity(ID id) {
        log.info("Desativando entidade ID: {}", id);
        T entity = getActiveEntityById(id);
        deactivateEntityInternal(entity);
        repository.save(entity);
        log.info("Entidade desativada com sucesso: ID {}", id);
    }

    @Override
    public T getEntityById(ID id) {
        log.info("Buscando entidade por ID (sem verificar estado): {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity with ID " + id + " not found"));
    }

    @Override
    public boolean isEntityActive(ID id) {
        T entity = getEntityById(id);
        return isEntityActiveInternal(entity);
    }

    @Override
    public long countActiveEntities() {
        return countActiveEntitiesInternal();
    }

    @Override
    public long countEntitiesByState(Long stateId) {
        return countEntitiesByStateInternal(stateId);
    }

    // Métodos abstratos que devem ser implementados pelas classes filhas
    protected abstract Page<T> getEntitiesPage(PaginationRequest request, Pageable pageable);
    protected abstract Page<T> getActiveEntitiesPage(PaginationRequest request, Pageable pageable);
    protected abstract Page<T> getEntitiesByStatePage(Long stateId, PaginationRequest request, Pageable pageable);
    protected abstract List<T> getActiveEntitiesList();
    protected abstract List<T> getEntitiesByStateList(Long stateId);
    protected abstract void deactivateEntityInternal(T entity);
    protected abstract boolean isEntityActiveInternal(T entity);
    protected abstract long countActiveEntitiesInternal();
    protected abstract long countEntitiesByStateInternal(Long stateId);
    protected abstract void validateStateExists(Long stateId);
    protected abstract R mapToResponse(T entity);

    // Métodos auxiliares para paginação
    protected Pageable createPageable(PaginationRequest request) {
        Sort sort = Sort.by(
            request.sortDirection().equalsIgnoreCase("DESC") ? 
                Sort.Direction.DESC : Sort.Direction.ASC,
            request.sortBy()
        );
        
        return PageRequest.of(request.page(), request.size(), sort);
    }

    protected PaginatedResponse<R> createPaginatedResponse(Page<T> entityPage, PaginationRequest request) {
        List<R> content = entityPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();
        
        return new PaginatedResponse<>(
            content,
            request.page(),
            request.size(),
            entityPage.getTotalElements()
        );
    }
}
