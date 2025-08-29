package com.automo.state.service;

import com.automo.state.dto.StateDto;
import com.automo.state.entity.State;
import com.automo.state.response.StateResponse;

import java.util.List;

public interface StateService {

    StateResponse createState(StateDto stateDto);

    StateResponse updateState(Long id, StateDto stateDto);

    List<StateResponse> getAllStates();

    State getStateById(Long id);

    StateResponse getStateByIdResponse(Long id);

    /**
     * Obtém estado por nome do estado
     */
    State getStateByState(String state);

    /**
     * Deleta um estado
     */
    void deleteState(Long id);
} 