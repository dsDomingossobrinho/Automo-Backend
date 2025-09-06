package com.automo.agent.service;

import com.automo.agent.dto.AgentDto;
import com.automo.agent.entity.Agent;
import com.automo.agent.repository.AgentRepository;
import com.automo.agent.response.AgentResponse;
import com.automo.agentAreas.response.AgentAreasResponse;
import com.automo.agentAreas.service.AgentAreasService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for AgentServiceImpl")
class AgentServiceImplTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private StateService stateService;

    @Mock
    private AgentAreasService agentAreasService;

    @InjectMocks
    private AgentServiceImpl agentService;

    private Agent testAgent;
    private State activeState;
    private State eliminatedState;
    private AgentDto testAgentDto;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        testAgent = TestDataFactory.createValidAgent(activeState);
        testAgent.setId(1L);
        testAgent.setCreatedAt(LocalDateTime.now());
        testAgent.setUpdatedAt(LocalDateTime.now());
        
        testAgentDto = new AgentDto(
            "Test Agent Updated",
            "Updated description",
            "Porto, Portugal",
            "Some restrictions",
            "Updated flow",
            1L
        );
    }

    @Test
    @DisplayName("Should create agent successfully")
    void shouldCreateAgentSuccessfully() {
        // Given
        when(stateService.findById(1L)).thenReturn(activeState);
        when(agentRepository.save(any(Agent.class))).thenReturn(testAgent);

        // When
        AgentResponse result = agentService.createAgent(testAgentDto);

        // Then
        assertNotNull(result);
        assertEquals("Test Agent", result.name()); // From TestDataFactory
        assertEquals("Test agent description", result.description()); // From TestDataFactory
        assertEquals(1L, result.stateId());
        assertEquals("ACTIVE", result.stateName());
        
        verify(stateService).findById(1L);
        verify(agentRepository).save(any(Agent.class));
    }

    @Test
    @DisplayName("Should throw exception when creating agent with invalid state")
    void shouldThrowExceptionWhenCreatingAgentWithInvalidState() {
        // Given
        when(stateService.findById(999L)).thenThrow(new EntityNotFoundException("State not found"));
        
        AgentDto invalidDto = new AgentDto(
            "Test Agent", "Description", "Location", "Restrictions", "Flow", 999L);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> agentService.createAgent(invalidDto));
        
        verify(stateService).findById(999L);
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    @DisplayName("Should update agent successfully")
    void shouldUpdateAgentSuccessfully() {
        // Given
        Long agentId = 1L;
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(agentRepository.save(any(Agent.class))).thenReturn(testAgent);

        // When
        AgentResponse result = agentService.updateAgent(agentId, testAgentDto);

        // Then
        assertNotNull(result);
        verify(agentRepository).findById(agentId);
        verify(stateService).findById(1L);
        verify(agentRepository).save(any(Agent.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing agent")
    void shouldThrowExceptionWhenUpdatingNonExistingAgent() {
        // Given
        Long agentId = 999L;
        when(agentRepository.findById(agentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> agentService.updateAgent(agentId, testAgentDto));
        
        verify(agentRepository).findById(agentId);
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    @DisplayName("Should get all agents excluding eliminated")
    void shouldGetAllAgentsExcludingEliminated() {
        // Given
        Agent agent1 = TestDataFactory.createValidAgent(activeState);
        agent1.setId(1L);
        agent1.setName("Agent 1");
        
        Agent agent2 = TestDataFactory.createValidAgent(activeState);
        agent2.setId(2L);
        agent2.setName("Agent 2");
        
        Agent eliminatedAgent = TestDataFactory.createValidAgent(eliminatedState);
        eliminatedAgent.setId(3L);
        eliminatedAgent.setName("Eliminated Agent");
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(agentRepository.findAllWithState()).thenReturn(Arrays.asList(agent1, agent2, eliminatedAgent));

        // When
        List<AgentResponse> result = agentService.getAllAgents();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Only active agents, eliminated should be filtered out
        assertTrue(result.stream().anyMatch(r -> r.name().equals("Agent 1")));
        assertTrue(result.stream().anyMatch(r -> r.name().equals("Agent 2")));
        assertFalse(result.stream().anyMatch(r -> r.name().equals("Eliminated Agent")));
        
        verify(stateService).getEliminatedState();
        verify(agentRepository).findAllWithState();
    }

    @Test
    @DisplayName("Should get agent by id successfully")
    void shouldGetAgentByIdSuccessfully() {
        // Given
        Long agentId = 1L;
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));

        // When
        Agent result = agentService.getAgentById(agentId);

        // Then
        assertNotNull(result);
        assertEquals(testAgent.getId(), result.getId());
        assertEquals(testAgent.getName(), result.getName());
        
        verify(agentRepository).findById(agentId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existing agent")
    void shouldThrowExceptionWhenGettingNonExistingAgent() {
        // Given
        Long agentId = 999L;
        when(agentRepository.findById(agentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> agentService.getAgentById(agentId));
        
        verify(agentRepository).findById(agentId);
    }

    @Test
    @DisplayName("Should get agent by id response successfully")
    void shouldGetAgentByIdResponseSuccessfully() {
        // Given
        Long agentId = 1L;
        when(agentRepository.findByIdWithState(agentId)).thenReturn(Optional.of(testAgent));

        // When
        AgentResponse result = agentService.getAgentByIdResponse(agentId);

        // Then
        assertNotNull(result);
        assertEquals(testAgent.getName(), result.name());
        assertEquals(testAgent.getDescription(), result.description());
        
        verify(agentRepository).findByIdWithState(agentId);
    }

    @Test
    @DisplayName("Should get agents by state successfully")
    void shouldGetAgentsByStateSuccessfully() {
        // Given
        Long stateId = 1L;
        List<Agent> agents = Arrays.asList(testAgent);
        when(agentRepository.findByStateIdWithState(stateId)).thenReturn(agents);

        // When
        List<AgentResponse> result = agentService.getAgentsByState(stateId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAgent.getName(), result.get(0).name());
        
        verify(agentRepository).findByStateIdWithState(stateId);
    }

    @Test
    @DisplayName("Should get agents by area successfully")
    void shouldGetAgentsByAreaSuccessfully() {
        // Given
        Long areaId = 1L;
        AgentAreasResponse agentAreaResponse = new AgentAreasResponse(1L, 1L, areaId, 1L, "ACTIVE", testAgent.getCreatedAt(), testAgent.getUpdatedAt());
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(agentAreasService.getAgentAreasByArea(areaId)).thenReturn(Collections.singletonList(agentAreaResponse));
        when(agentRepository.findByIdWithState(1L)).thenReturn(Optional.of(testAgent));

        // When
        List<AgentResponse> result = agentService.getAgentsByArea(areaId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAgent.getName(), result.get(0).name());
        
        verify(agentAreasService).getAgentAreasByArea(areaId);
        verify(agentRepository, times(2)).findByIdWithState(1L); // Called twice in the method
    }

    @Test
    @DisplayName("Should filter out eliminated agents when getting by area")
    void shouldFilterOutEliminatedAgentsWhenGettingByArea() {
        // Given
        Long areaId = 1L;
        Agent eliminatedAgent = TestDataFactory.createValidAgent(eliminatedState);
        eliminatedAgent.setId(2L);
        
        AgentAreasResponse agentAreaResponse = new AgentAreasResponse(1L, 2L, areaId, 4L, "ELIMINATED", testAgent.getCreatedAt(), testAgent.getUpdatedAt());
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(agentAreasService.getAgentAreasByArea(areaId)).thenReturn(Collections.singletonList(agentAreaResponse));
        when(agentRepository.findByIdWithState(2L)).thenReturn(Optional.of(eliminatedAgent));

        // When
        List<AgentResponse> result = agentService.getAgentsByArea(areaId);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size()); // Eliminated agent should be filtered out
        
        verify(agentAreasService).getAgentAreasByArea(areaId);
        verify(agentRepository, times(2)).findByIdWithState(2L);
    }

    @Test
    @DisplayName("Should search agents by name successfully")
    void shouldSearchAgentsByNameSuccessfully() {
        // Given
        String searchName = "Test";
        List<Agent> agents = Arrays.asList(testAgent);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(agentRepository.findByNameContainingIgnoreCaseWithState(searchName)).thenReturn(agents);

        // When
        List<AgentResponse> result = agentService.searchAgentsByName(searchName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAgent.getName(), result.get(0).name());
        
        verify(agentRepository).findByNameContainingIgnoreCaseWithState(searchName);
    }

    @Test
    @DisplayName("Should filter out eliminated agents in search")
    void shouldFilterOutEliminatedAgentsInSearch() {
        // Given
        String searchName = "Test";
        Agent eliminatedAgent = TestDataFactory.createValidAgent(eliminatedState);
        eliminatedAgent.setId(2L);
        eliminatedAgent.setName("Test Eliminated Agent");
        
        List<Agent> agents = Arrays.asList(testAgent, eliminatedAgent);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(agentRepository.findByNameContainingIgnoreCaseWithState(searchName)).thenReturn(agents);

        // When
        List<AgentResponse> result = agentService.searchAgentsByName(searchName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Only active agent, eliminated should be filtered out
        assertEquals(testAgent.getName(), result.get(0).name());
        
        verify(agentRepository).findByNameContainingIgnoreCaseWithState(searchName);
    }

    @Test
    @DisplayName("Should soft delete agent successfully")
    void shouldSoftDeleteAgentSuccessfully() {
        // Given
        Long agentId = 1L;
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(agentRepository.save(any(Agent.class))).thenReturn(testAgent);

        // When
        agentService.deleteAgent(agentId);

        // Then
        verify(agentRepository).findById(agentId);
        verify(stateService).getEliminatedState();
        verify(agentRepository).save(testAgent);
        assertEquals(eliminatedState, testAgent.getState());
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing agent")
    void shouldThrowExceptionWhenDeletingNonExistingAgent() {
        // Given
        Long agentId = 999L;
        when(agentRepository.findById(agentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> agentService.deleteAgent(agentId));
        
        verify(agentRepository).findById(agentId);
        verify(stateService, never()).getEliminatedState();
        verify(agentRepository, never()).save(any(Agent.class));
    }

    @Test
    @DisplayName("Should implement findById method correctly")
    void shouldImplementFindByIdMethodCorrectly() {
        // Given
        Long agentId = 1L;
        when(agentRepository.findByIdWithState(agentId)).thenReturn(Optional.of(testAgent));

        // When
        Agent result = agentService.findById(agentId);

        // Then
        assertNotNull(result);
        assertEquals(testAgent, result);
        verify(agentRepository).findByIdWithState(agentId);
    }

    @Test
    @DisplayName("Should throw exception in findById when agent not found")
    void shouldThrowExceptionInFindByIdWhenAgentNotFound() {
        // Given
        Long agentId = 999L;
        when(agentRepository.findByIdWithState(agentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> agentService.findById(agentId));
        verify(agentRepository).findByIdWithState(agentId);
    }

    @Test
    @DisplayName("Should implement findByIdAndStateId method correctly")
    void shouldImplementFindByIdAndStateIdMethodCorrectly() {
        // Given
        Long agentId = 1L;
        Long stateId = 1L;
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));

        // When
        Agent result = agentService.findByIdAndStateId(agentId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testAgent, result);
        verify(agentRepository).findById(agentId);
    }

    @Test
    @DisplayName("Should throw exception in findByIdAndStateId when states don't match")
    void shouldThrowExceptionInFindByIdAndStateIdWhenStatesDontMatch() {
        // Given
        Long agentId = 1L;
        Long stateId = 2L; // Different from agent's state (1L)
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> agentService.findByIdAndStateId(agentId, stateId));
        
        verify(agentRepository).findById(agentId);
    }

    @Test
    @DisplayName("Should use default state in findByIdAndStateId when stateId is null")
    void shouldUseDefaultStateInFindByIdAndStateIdWhenStateIdIsNull() {
        // Given
        Long agentId = 1L;
        Long stateId = null;
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));

        // When
        Agent result = agentService.findByIdAndStateId(agentId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testAgent, result);
        verify(agentRepository).findById(agentId);
    }

    @Test
    @DisplayName("Should handle empty result when getting all agents")
    void shouldHandleEmptyResultWhenGettingAllAgents() {
        // Given
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(agentRepository.findAllWithState()).thenReturn(Collections.emptyList());

        // When
        List<AgentResponse> result = agentService.getAllAgents();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(stateService).getEliminatedState();
        verify(agentRepository).findAllWithState();
    }

    @Test
    @DisplayName("Should handle empty result when searching agents by name")
    void shouldHandleEmptyResultWhenSearchingAgentsByName() {
        // Given
        String searchName = "NonExistent";
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(agentRepository.findByNameContainingIgnoreCaseWithState(searchName)).thenReturn(Collections.emptyList());

        // When
        List<AgentResponse> result = agentService.searchAgentsByName(searchName);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(agentRepository).findByNameContainingIgnoreCaseWithState(searchName);
    }

    @Test
    @DisplayName("Should handle empty result when getting agents by state")
    void shouldHandleEmptyResultWhenGettingAgentsByState() {
        // Given
        Long stateId = 999L;
        when(agentRepository.findByStateIdWithState(stateId)).thenReturn(Collections.emptyList());

        // When
        List<AgentResponse> result = agentService.getAgentsByState(stateId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(agentRepository).findByStateIdWithState(stateId);
    }

    @Test
    @DisplayName("Should handle empty result when getting agents by area")
    void shouldHandleEmptyResultWhenGettingAgentsByArea() {
        // Given
        Long areaId = 999L;
        when(agentAreasService.getAgentAreasByArea(areaId)).thenReturn(Collections.emptyList());

        // When
        List<AgentResponse> result = agentService.getAgentsByArea(areaId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(agentAreasService).getAgentAreasByArea(areaId);
    }

    @Test
    @DisplayName("Should map to response correctly")
    void shouldMapToResponseCorrectly() {
        // Given - using the testAgent with all required fields
        testAgent.setCreatedAt(LocalDateTime.now());
        testAgent.setUpdatedAt(LocalDateTime.now());

        // When - Call private method via public method
        AgentResponse result = agentService.getAgentByIdResponse(testAgent.getId());

        // Mock the repository to return our test agent
        when(agentRepository.findByIdWithState(testAgent.getId())).thenReturn(Optional.of(testAgent));
        result = agentService.getAgentByIdResponse(testAgent.getId());

        // Then
        assertEquals(testAgent.getId(), result.id());
        assertEquals(testAgent.getName(), result.name());
        assertEquals(testAgent.getDescription(), result.description());
        assertEquals(testAgent.getLocation(), result.location());
        assertEquals(testAgent.getRestrictions(), result.restrictions());
        assertEquals(testAgent.getActivityFlow(), result.activityFlow());
        assertEquals(testAgent.getState().getId(), result.stateId());
        assertEquals(testAgent.getState().getState(), result.stateName());
        assertEquals(testAgent.getCreatedAt(), result.createdAt());
        assertEquals(testAgent.getUpdatedAt(), result.updatedAt());
    }
}