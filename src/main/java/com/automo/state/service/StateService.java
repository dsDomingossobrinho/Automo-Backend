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

    State getStateByState(String state);

    void deleteState(Long id);
} 