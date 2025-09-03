package com.automo.state.service;

import com.automo.state.dto.StateDto;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.state.response.StateResponse;
import com.automo.model.service.BaseServiceImpl;
import com.automo.model.dto.PaginationRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class StateServiceImpl extends BaseServiceImpl<State, StateResponse, Long> implements StateService {

    private final StateRepository stateRepository;
    
    public StateServiceImpl(StateRepository stateRepository) {
        super(stateRepository);
        this.stateRepository = stateRepository;
    }

    @Override
    @Transactional
    public StateResponse createState(StateDto stateDto) {
        State state = new State();
        state.setState(stateDto.state());
        state.setDescription(stateDto.description());
        
        State savedState = stateRepository.save(state);
        return mapToResponse(savedState);
    }

    @Override
    @Transactional
    public StateResponse updateState(Long id, StateDto stateDto) {
        State state = this.getStateById(id);
        
        state.setState(stateDto.state());
        state.setDescription(stateDto.description());
        
        State updatedState = stateRepository.save(state);
        return mapToResponse(updatedState);
    }

    @Override
    public List<StateResponse> getAllStates() {
        return stateRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public State getStateById(Long id) {
        return stateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + id + " not found"));
    }

    @Override
    public StateResponse getStateByIdResponse(Long id) {
        State state = this.getStateById(id);
        return mapToResponse(state);
    }

    @Override
    public State getStateByState(String state) {
        return stateRepository.findByState(state)
                .orElseThrow(() -> new EntityNotFoundException("State " + state + " not found"));
    }

    @Override
    @Transactional
    public void deleteState(Long id) {
        State state = this.getStateById(id);
        stateRepository.delete(state);
    }

    
    @Override
    public State findById(Long id) {
        return stateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + id + " not found"));
    }
    
    @Override
    public State findByIdAndStateId(Long id, Long stateId) {
        State entity = stateRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("State with ID " + id + " not found"));
        
        // For State entity, return the entity regardless of stateId parameter
        return entity;
    }
    
    @Override
    public State getEliminatedState() {
        return stateRepository.findByState("ELIMINATED")
                .orElseThrow(() -> new EntityNotFoundException("ELIMINATED state not found"));
    }

    // Implementações dos métodos abstratos do BaseServiceImpl
    @Override
    protected Page<State> getEntitiesPage(PaginationRequest request, Pageable pageable) {
        if (request.search() == null || request.search().trim().isEmpty()) {
            return stateRepository.findAll(pageable);
        }
        return stateRepository.findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                request.search().trim(), request.search().trim(), pageable);
    }

    @Override
    protected Page<State> getActiveEntitiesPage(PaginationRequest request, Pageable pageable) {
        // Para States, todos são considerados "ativos" pois não têm estado próprio
        return getEntitiesPage(request, pageable);
    }

    @Override
    protected Page<State> getEntitiesByStatePage(Long stateId, PaginationRequest request, Pageable pageable) {
        // Para States, não aplicável pois não têm estado, mas retorna todos para compatibilidade
        return getEntitiesPage(request, pageable);
    }

    @Override
    protected List<State> getActiveEntitiesList() {
        return stateRepository.findAll();
    }

    @Override
    protected List<State> getEntitiesByStateList(Long stateId) {
        return stateRepository.findAll();
    }

    @Override
    protected void deactivateEntityInternal(State entity) {
        // Para States, não há desativação real pois não têm estado próprio
        log.warn("Tentativa de desativar State ID: {}. States não possuem estado próprio para desativação.", entity.getId());
        throw new UnsupportedOperationException("States não suportam desativação pois não possuem estado próprio");
    }

    @Override
    protected boolean isEntityActiveInternal(State entity) {
        // Para States, todos são considerados "ativos" pois não têm estado próprio
        return true;
    }

    @Override
    protected long countActiveEntitiesInternal() {
        return stateRepository.count();
    }

    @Override
    protected long countEntitiesByStateInternal(Long stateId) {
        // Para States, não aplicável, mas retorna count total para compatibilidade
        return stateRepository.count();
    }

    @Override
    protected void validateStateExists(Long stateId) {
        // Para States, não aplicável pois não têm estado próprio
        log.debug("Validação de estado não aplicável para States. StateId: {}", stateId);
    }

    @Override
    public StateResponse mapToResponse(State state) {
        return new StateResponse(
                state.getId(),
                state.getState(),
                state.getDescription(),
                state.getCreatedAt(),
                state.getUpdatedAt()
        );
    }
} 