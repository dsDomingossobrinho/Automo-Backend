package com.automo.state.controller;

import com.automo.config.security.JwtUtils;
import com.automo.state.dto.StateDto;
import com.automo.state.entity.State;
import com.automo.state.response.StateResponse;
import com.automo.state.service.StateService;
import com.automo.test.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StateController.class)
@DisplayName("Tests for StateController")
class StateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StateService stateService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private StateResponse stateResponse;
    private StateDto stateDto;
    private State state;

    @BeforeEach
    void setUp() {
        state = TestDataFactory.createActiveState();
        state.setId(1L);
        state.setDescription("Active state");

        stateResponse = new StateResponse(
                1L,
                "ACTIVE",
                "Active state",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        stateDto = TestDataFactory.createValidStateDto("ACTIVE", "Active state");
    }

    @Test
    @DisplayName("Should get all states successfully")
    void shouldGetAllStatesSuccessfully() throws Exception {
        // Given
        StateResponse inactiveResponse = new StateResponse(
                2L,
                "INACTIVE",
                "Inactive state",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        List<StateResponse> stateResponses = Arrays.asList(stateResponse, inactiveResponse);
        when(stateService.getAllStates()).thenReturn(stateResponses);

        // When & Then
        mockMvc.perform(get("/states")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].state").value("ACTIVE"))
                .andExpect(jsonPath("$[0].description").value("Active state"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].state").value("INACTIVE"));

        verify(stateService).getAllStates();
    }

    @Test
    @DisplayName("Should get state by ID successfully")
    void shouldGetStateByIdSuccessfully() throws Exception {
        // Given
        when(stateService.getStateByIdResponse(1L)).thenReturn(stateResponse);

        // When & Then
        mockMvc.perform(get("/states/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("ACTIVE"))
                .andExpect(jsonPath("$.description").value("Active state"));

        verify(stateService).getStateByIdResponse(1L);
    }

    @Test
    @DisplayName("Should return not found when state ID doesn't exist")
    void shouldReturnNotFoundWhenStateIdDoesntExist() throws Exception {
        // Given
        when(stateService.getStateByIdResponse(999L))
                .thenThrow(new EntityNotFoundException("State with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/states/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(stateService).getStateByIdResponse(999L);
    }

    @Test
    @DisplayName("Should get state by name successfully")
    void shouldGetStateByNameSuccessfully() throws Exception {
        // Given
        when(stateService.getStateByState("ACTIVE")).thenReturn(state);
        when(stateService.getStateByIdResponse(1L)).thenReturn(stateResponse);

        // When & Then
        mockMvc.perform(get("/states/name/ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("ACTIVE"))
                .andExpect(jsonPath("$.description").value("Active state"));

        verify(stateService).getStateByState("ACTIVE");
        verify(stateService).getStateByIdResponse(1L);
    }

    @Test
    @DisplayName("Should return not found when state name doesn't exist")
    void shouldReturnNotFoundWhenStateNameDoesntExist() throws Exception {
        // Given
        when(stateService.getStateByState("UNKNOWN"))
                .thenThrow(new EntityNotFoundException("State UNKNOWN not found"));

        // When & Then
        mockMvc.perform(get("/states/name/UNKNOWN")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(stateService).getStateByState("UNKNOWN");
        verify(stateService, never()).getStateByIdResponse(any());
    }

    @Test
    @DisplayName("Should create state successfully when user is admin")
    void shouldCreateStateSuccessfullyWhenUserIsAdmin() throws Exception {
        // Given
        when(jwtUtils.isCurrentUserAdmin()).thenReturn(true);
        when(stateService.createState(any(StateDto.class))).thenReturn(stateResponse);

        // When & Then
        mockMvc.perform(post("/states")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stateDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("ACTIVE"))
                .andExpect(jsonPath("$.description").value("Active state"));

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService).createState(any(StateDto.class));
    }

    @Test
    @DisplayName("Should return forbidden when creating state and user is not admin")
    void shouldReturnForbiddenWhenCreatingStateAndUserIsNotAdmin() throws Exception {
        // Given
        when(jwtUtils.isCurrentUserAdmin()).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/states")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stateDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService, never()).createState(any(StateDto.class));
    }

    @Test
    @DisplayName("Should return bad request when creating state with invalid data")
    void shouldReturnBadRequestWhenCreatingStateWithInvalidData() throws Exception {
        // Given
        when(jwtUtils.isCurrentUserAdmin()).thenReturn(true);
        StateDto invalidDto = TestDataFactory.createValidStateDto(null, "Description");

        // When & Then
        mockMvc.perform(post("/states")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService, never()).createState(any(StateDto.class));
    }

    @Test
    @DisplayName("Should update state successfully when user is admin")
    void shouldUpdateStateSuccessfullyWhenUserIsAdmin() throws Exception {
        // Given
        StateDto updateDto = TestDataFactory.createValidStateDto("UPDATED", "Updated state");
        StateResponse updatedResponse = new StateResponse(
                1L,
                "UPDATED",
                "Updated state",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(jwtUtils.isCurrentUserAdmin()).thenReturn(true);
        when(stateService.updateState(eq(1L), any(StateDto.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/states/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.state").value("UPDATED"))
                .andExpect(jsonPath("$.description").value("Updated state"));

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService).updateState(eq(1L), any(StateDto.class));
    }

    @Test
    @DisplayName("Should return forbidden when updating state and user is not admin")
    void shouldReturnForbiddenWhenUpdatingStateAndUserIsNotAdmin() throws Exception {
        // Given
        when(jwtUtils.isCurrentUserAdmin()).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/states/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stateDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService, never()).updateState(any(), any(StateDto.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent state")
    void shouldReturnNotFoundWhenUpdatingNonExistentState() throws Exception {
        // Given
        when(jwtUtils.isCurrentUserAdmin()).thenReturn(true);
        when(stateService.updateState(eq(999L), any(StateDto.class)))
                .thenThrow(new EntityNotFoundException("State with ID 999 not found"));

        // When & Then
        mockMvc.perform(put("/states/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stateDto)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService).updateState(eq(999L), any(StateDto.class));
    }

    @Test
    @DisplayName("Should delete state successfully when user is admin")
    void shouldDeleteStateSuccessfullyWhenUserIsAdmin() throws Exception {
        // Given
        when(jwtUtils.isCurrentUserAdmin()).thenReturn(true);
        doNothing().when(stateService).deleteState(1L);

        // When & Then
        mockMvc.perform(delete("/states/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService).deleteState(1L);
    }

    @Test
    @DisplayName("Should return forbidden when deleting state and user is not admin")
    void shouldReturnForbiddenWhenDeletingStateAndUserIsNotAdmin() throws Exception {
        // Given
        when(jwtUtils.isCurrentUserAdmin()).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/states/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService, never()).deleteState(any());
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent state")
    void shouldReturnNotFoundWhenDeletingNonExistentState() throws Exception {
        // Given
        when(jwtUtils.isCurrentUserAdmin()).thenReturn(true);
        doThrow(new EntityNotFoundException("State with ID 999 not found"))
                .when(stateService).deleteState(999L);

        // When & Then
        mockMvc.perform(delete("/states/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService).deleteState(999L);
    }

    @Test
    @DisplayName("Should handle empty state list")
    void shouldHandleEmptyStateList() throws Exception {
        // Given
        when(stateService.getAllStates()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/states")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(stateService).getAllStates();
    }

    @Test
    @DisplayName("Should handle invalid JSON in request body")
    void shouldHandleInvalidJsonInRequestBody() throws Exception {
        // Given
        when(jwtUtils.isCurrentUserAdmin()).thenReturn(true);
        String invalidJson = "{\"state\": \"ACTIVE\", \"description\": }"; // Invalid JSON

        // When & Then
        mockMvc.perform(post("/states")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(jwtUtils).isCurrentUserAdmin();
        verify(stateService, never()).createState(any(StateDto.class));
    }

    @Test
    @DisplayName("Should handle special characters in state name URL parameter")
    void shouldHandleSpecialCharactersInStateNameUrlParameter() throws Exception {
        // Given
        String stateNameWithSpaces = "PENDING PAYMENT";
        State stateWithSpaces = new State();
        stateWithSpaces.setId(3L);
        stateWithSpaces.setState(stateNameWithSpaces);
        
        StateResponse responseWithSpaces = new StateResponse(
                3L,
                stateNameWithSpaces,
                "Pending payment state",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(stateService.getStateByState(stateNameWithSpaces)).thenReturn(stateWithSpaces);
        when(stateService.getStateByIdResponse(3L)).thenReturn(responseWithSpaces);

        // When & Then
        mockMvc.perform(get("/states/name/{stateName}", stateNameWithSpaces)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.state").value(stateNameWithSpaces));

        verify(stateService).getStateByState(stateNameWithSpaces);
        verify(stateService).getStateByIdResponse(3L);
    }
}