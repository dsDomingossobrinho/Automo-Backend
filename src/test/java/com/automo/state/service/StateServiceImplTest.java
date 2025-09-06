package com.automo.state.service;

import com.automo.state.dto.StateDto;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.state.response.StateResponse;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for StateServiceImpl")
class StateServiceImplTest {

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateServiceImpl stateService;

    private State testState;
    private StateDto testStateDto;

    @BeforeEach
    void setUp() {
        testState = TestDataFactory.createActiveState();
        testState.setId(1L);
        testState.setDescription("Active state");

        testStateDto = TestDataFactory.createValidStateDto("ACTIVE", "Active state");
    }

    @Test
    @DisplayName("Should create state successfully")
    void shouldCreateStateSuccessfully() {
        // Given
        when(stateRepository.save(any(State.class))).thenReturn(testState);

        // When
        StateResponse response = stateService.createState(testStateDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("ACTIVE", response.state());
        assertEquals("Active state", response.description());

        verify(stateRepository).save(any(State.class));
    }

    @Test
    @DisplayName("Should create state with null description")
    void shouldCreateStateWithNullDescription() {
        // Given
        StateDto dtoWithNullDescription = TestDataFactory.createValidStateDto("INACTIVE", null);
        State stateWithNullDescription = TestDataFactory.createInactiveState();
        stateWithNullDescription.setId(2L);

        when(stateRepository.save(any(State.class))).thenReturn(stateWithNullDescription);

        // When
        StateResponse response = stateService.createState(dtoWithNullDescription);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.id());
        assertEquals("INACTIVE", response.state());
        assertNull(response.description());

        verify(stateRepository).save(any(State.class));
    }

    @Test
    @DisplayName("Should update state successfully")
    void shouldUpdateStateSuccessfully() {
        // Given
        StateDto updateDto = TestDataFactory.createValidStateDto("PENDING", "Pending state");
        
        when(stateRepository.findById(1L)).thenReturn(Optional.of(testState));
        when(stateRepository.save(any(State.class))).thenReturn(testState);

        // When
        StateResponse response = stateService.updateState(1L, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("PENDING", testState.getState());
        assertEquals("Pending state", testState.getDescription());

        verify(stateRepository).findById(1L);
        verify(stateRepository).save(testState);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent state")
    void shouldThrowExceptionWhenUpdatingNonExistentState() {
        // Given
        when(stateRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stateService.updateState(999L, testStateDto));

        assertEquals("State with ID 999 not found", exception.getMessage());
        verify(stateRepository).findById(999L);
        verify(stateRepository, never()).save(any(State.class));
    }

    @Test
    @DisplayName("Should get all states")
    void shouldGetAllStates() {
        // Given
        State inactiveState = TestDataFactory.createInactiveState();
        inactiveState.setId(2L);
        
        List<State> states = Arrays.asList(testState, inactiveState);
        when(stateRepository.findAll()).thenReturn(states);

        // When
        List<StateResponse> responses = stateService.getAllStates();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        StateResponse firstResponse = responses.get(0);
        assertEquals(1L, firstResponse.id());
        assertEquals("ACTIVE", firstResponse.state());
        
        StateResponse secondResponse = responses.get(1);
        assertEquals(2L, secondResponse.id());
        assertEquals("INACTIVE", secondResponse.state());

        verify(stateRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no states exist")
    void shouldReturnEmptyListWhenNoStatesExist() {
        // Given
        when(stateRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<StateResponse> responses = stateService.getAllStates();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(stateRepository).findAll();
    }

    @Test
    @DisplayName("Should get state by ID")
    void shouldGetStateById() {
        // Given
        when(stateRepository.findById(1L)).thenReturn(Optional.of(testState));

        // When
        State result = stateService.getStateById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ACTIVE", result.getState());

        verify(stateRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when state not found by ID")
    void shouldThrowExceptionWhenStateNotFoundById() {
        // Given
        when(stateRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stateService.getStateById(999L));

        assertEquals("State with ID 999 not found", exception.getMessage());
        verify(stateRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get state by ID response")
    void shouldGetStateByIdResponse() {
        // Given
        when(stateRepository.findById(1L)).thenReturn(Optional.of(testState));

        // When
        StateResponse response = stateService.getStateByIdResponse(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("ACTIVE", response.state());
        assertEquals("Active state", response.description());

        verify(stateRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get state by state name")
    void shouldGetStateByStateName() {
        // Given
        when(stateRepository.findByState("ACTIVE")).thenReturn(Optional.of(testState));

        // When
        State result = stateService.getStateByState("ACTIVE");

        // Then
        assertNotNull(result);
        assertEquals("ACTIVE", result.getState());
        assertEquals("Active state", result.getDescription());

        verify(stateRepository).findByState("ACTIVE");
    }

    @Test
    @DisplayName("Should throw exception when state not found by name")
    void shouldThrowExceptionWhenStateNotFoundByName() {
        // Given
        when(stateRepository.findByState("UNKNOWN")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stateService.getStateByState("UNKNOWN"));

        assertEquals("State UNKNOWN not found", exception.getMessage());
        verify(stateRepository).findByState("UNKNOWN");
    }

    @Test
    @DisplayName("Should delete state successfully")
    void shouldDeleteStateSuccessfully() {
        // Given
        when(stateRepository.findById(1L)).thenReturn(Optional.of(testState));

        // When
        stateService.deleteState(1L);

        // Then
        verify(stateRepository).findById(1L);
        verify(stateRepository).delete(testState);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent state")
    void shouldThrowExceptionWhenDeletingNonExistentState() {
        // Given
        when(stateRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stateService.deleteState(999L));

        assertEquals("State with ID 999 not found", exception.getMessage());
        verify(stateRepository).findById(999L);
        verify(stateRepository, never()).delete(any(State.class));
    }

    @Test
    @DisplayName("Should find by ID using findById method")
    void shouldFindByIdUsingFindByIdMethod() {
        // Given
        when(stateRepository.findById(1L)).thenReturn(Optional.of(testState));

        // When
        State result = stateService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ACTIVE", result.getState());

        verify(stateRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception in findById when not found")
    void shouldThrowExceptionInFindByIdWhenNotFound() {
        // Given
        when(stateRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stateService.findById(999L));

        assertEquals("State with ID 999 not found", exception.getMessage());
        verify(stateRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find by ID and state ID ignoring state ID for State")
    void shouldFindByIdAndStateIdIgnoringStateIdForState() {
        // Given - State doesn't have state relationship, so stateId should be ignored
        when(stateRepository.findById(1L)).thenReturn(Optional.of(testState));

        // When
        State result = stateService.findByIdAndStateId(1L, 99L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ACTIVE", result.getState());

        verify(stateRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get eliminated state")
    void shouldGetEliminatedState() {
        // Given
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(3L);
        when(stateRepository.findByState("ELIMINATED")).thenReturn(Optional.of(eliminatedState));

        // When
        State result = stateService.getEliminatedState();

        // Then
        assertNotNull(result);
        assertEquals("ELIMINATED", result.getState());

        verify(stateRepository).findByState("ELIMINATED");
    }

    @Test
    @DisplayName("Should throw exception when eliminated state not found")
    void shouldThrowExceptionWhenEliminatedStateNotFound() {
        // Given
        when(stateRepository.findByState("ELIMINATED")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stateService.getEliminatedState());

        assertEquals("ELIMINATED state not found", exception.getMessage());
        verify(stateRepository).findByState("ELIMINATED");
    }

    @Test
    @DisplayName("Should handle case-sensitive state searches")
    void shouldHandleCaseSensitiveStateSearches() {
        // Given
        when(stateRepository.findByState("active")).thenReturn(Optional.empty());
        when(stateRepository.findByState("ACTIVE")).thenReturn(Optional.of(testState));

        // When & Then
        EntityNotFoundException lowerCaseException = assertThrows(EntityNotFoundException.class, () ->
                stateService.getStateByState("active"));
        assertEquals("State active not found", lowerCaseException.getMessage());

        State upperCaseResult = stateService.getStateByState("ACTIVE");
        assertEquals("ACTIVE", upperCaseResult.getState());

        verify(stateRepository).findByState("active");
        verify(stateRepository).findByState("ACTIVE");
    }

    @Test
    @DisplayName("Should map entity to response correctly")
    void shouldMapEntityToResponseCorrectly() {
        // Given
        testState.setId(10L);
        testState.setState("PENDING");
        testState.setDescription("Pending state description");
        
        when(stateRepository.findById(10L)).thenReturn(Optional.of(testState));

        // When
        StateResponse response = stateService.getStateByIdResponse(10L);

        // Then
        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("PENDING", response.state());
        assertEquals("Pending state description", response.description());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());

        verify(stateRepository).findById(10L);
    }

    @Test
    @DisplayName("Should handle special characters in state and description")
    void shouldHandleSpecialCharactersInStateAndDescription() {
        // Given
        StateDto specialCharsDto = TestDataFactory.createValidStateDto(
                "PENDING-APPROVAL_2024", 
                "Pending Approval (2024) - Special Case & Additional Info!"
        );
        
        State specialCharsState = new State();
        specialCharsState.setId(5L);
        specialCharsState.setState("PENDING-APPROVAL_2024");
        specialCharsState.setDescription("Pending Approval (2024) - Special Case & Additional Info!");
        
        when(stateRepository.save(any(State.class))).thenReturn(specialCharsState);

        // When
        StateResponse response = stateService.createState(specialCharsDto);

        // Then
        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals("PENDING-APPROVAL_2024", response.state());
        assertEquals("Pending Approval (2024) - Special Case & Additional Info!", response.description());

        verify(stateRepository).save(any(State.class));
    }

    @Test
    @DisplayName("Should handle null state search gracefully")
    void shouldHandleNullStateSearchGracefully() {
        // Given
        when(stateRepository.findByState(null)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                stateService.getStateByState(null));

        assertEquals("State null not found", exception.getMessage());
        verify(stateRepository).findByState(null);
    }

    @Test
    @DisplayName("Should create state with maximum length values")
    void shouldCreateStateWithMaximumLengthValues() {
        // Given
        String longState = "A".repeat(255); // Assuming max length constraint
        String longDescription = "B".repeat(1000); // Assuming max length constraint
        
        StateDto longDto = TestDataFactory.createValidStateDto(longState, longDescription);
        State longState1 = new State();
        longState1.setId(6L);
        longState1.setState(longState);
        longState1.setDescription(longDescription);
        
        when(stateRepository.save(any(State.class))).thenReturn(longState1);

        // When
        StateResponse response = stateService.createState(longDto);

        // Then
        assertNotNull(response);
        assertEquals(6L, response.id());
        assertEquals(longState, response.state());
        assertEquals(longDescription, response.description());

        verify(stateRepository).save(any(State.class));
    }

    @Test
    @DisplayName("Should handle concurrent updates correctly")
    void shouldHandleConcurrentUpdatesCorrectly() {
        // Given
        StateDto updateDto1 = TestDataFactory.createValidStateDto("STATE1", "Description 1");
        StateDto updateDto2 = TestDataFactory.createValidStateDto("STATE2", "Description 2");
        
        when(stateRepository.findById(1L)).thenReturn(Optional.of(testState));
        when(stateRepository.save(any(State.class))).thenReturn(testState);

        // When
        StateResponse response1 = stateService.updateState(1L, updateDto1);
        StateResponse response2 = stateService.updateState(1L, updateDto2);

        // Then
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(1L, response1.id());
        assertEquals(1L, response2.id());
        
        // The last update should win
        assertEquals("STATE2", testState.getState());
        assertEquals("Description 2", testState.getDescription());

        verify(stateRepository, times(2)).findById(1L);
        verify(stateRepository, times(2)).save(testState);
    }
}