package com.automo.state.service;

import com.automo.state.dto.StateDto;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.state.response.StateResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    @Override
    public StateResponse createState(StateDto stateDto) {
        State state = new State();
        state.setState(stateDto.state());
        state.setDescription(stateDto.description());
        
        State savedState = stateRepository.save(state);
        return mapToResponse(savedState);
    }

    @Override
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
    public void deleteState(Long id) {
        State state = this.getStateById(id);
        stateRepository.delete(state);
    }

    private StateResponse mapToResponse(State state) {
        return new StateResponse(
                state.getId(),
                state.getState(),
                state.getDescription(),
                state.getCreatedAt(),
                state.getUpdatedAt()
        );
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
} 