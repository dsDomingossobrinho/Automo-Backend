package com.automo.agent.repository;

import com.automo.agent.entity.Agent;
import com.automo.state.entity.State;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for AgentRepository")
class AgentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AgentRepository agentRepository;

    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private Agent testAgent1;
    private Agent testAgent2;
    private Agent testAgent3;

    @BeforeEach
    void setUp() {
        // Create and persist states
        activeState = TestDataFactory.createActiveState();
        activeState = entityManager.persistAndFlush(activeState);

        inactiveState = TestDataFactory.createInactiveState();
        inactiveState = entityManager.persistAndFlush(inactiveState);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = entityManager.persistAndFlush(eliminatedState);

        // Create and persist agents
        testAgent1 = TestDataFactory.createValidAgent(activeState);
        testAgent1.setName("Agent One");
        testAgent1.setDescription("Description for Agent One");
        testAgent1.setLocation("Lisboa");
        testAgent1 = entityManager.persistAndFlush(testAgent1);

        testAgent2 = TestDataFactory.createValidAgent(activeState);
        testAgent2.setName("Agent Two");
        testAgent2.setDescription("Description for Agent Two");
        testAgent2.setLocation("Porto");
        testAgent2 = entityManager.persistAndFlush(testAgent2);

        testAgent3 = TestDataFactory.createValidAgent(inactiveState);
        testAgent3.setName("Inactive Agent");
        testAgent3.setDescription("Inactive agent description");
        testAgent3.setLocation("Coimbra");
        testAgent3 = entityManager.persistAndFlush(testAgent3);
    }

    @Test
    @DisplayName("Should find all agents with state eagerly loaded")
    void shouldFindAllAgentsWithStateEagerlyLoaded() {
        // When
        List<Agent> agents = agentRepository.findAllWithState();

        // Then
        assertNotNull(agents);
        assertEquals(3, agents.size());
        
        // Verify all agents have state loaded
        agents.forEach(agent -> {
            assertNotNull(agent.getState());
            assertNotNull(agent.getState().getState());
        });

        // Verify specific agents
        assertTrue(agents.stream().anyMatch(a -> a.getName().equals("Agent One")));
        assertTrue(agents.stream().anyMatch(a -> a.getName().equals("Agent Two")));
        assertTrue(agents.stream().anyMatch(a -> a.getName().equals("Inactive Agent")));
    }

    @Test
    @DisplayName("Should find agent by id with state eagerly loaded")
    void shouldFindAgentByIdWithStateEagerlyLoaded() {
        // When
        Optional<Agent> foundAgent = agentRepository.findByIdWithState(testAgent1.getId());

        // Then
        assertTrue(foundAgent.isPresent());
        Agent agent = foundAgent.get();
        assertEquals(testAgent1.getId(), agent.getId());
        assertEquals("Agent One", agent.getName());
        assertEquals("Description for Agent One", agent.getDescription());
        assertEquals("Lisboa", agent.getLocation());
        
        // Verify state is eagerly loaded
        assertNotNull(agent.getState());
        assertEquals("ACTIVE", agent.getState().getState());
    }

    @Test
    @DisplayName("Should return empty when finding non-existing agent by id")
    void shouldReturnEmptyWhenFindingNonExistingAgentById() {
        // When
        Optional<Agent> foundAgent = agentRepository.findByIdWithState(999L);

        // Then
        assertFalse(foundAgent.isPresent());
    }

    @Test
    @DisplayName("Should find agents by state id with state eagerly loaded")
    void shouldFindAgentsByStateIdWithStateEagerlyLoaded() {
        // When
        List<Agent> activeAgents = agentRepository.findByStateIdWithState(activeState.getId());

        // Then
        assertNotNull(activeAgents);
        assertEquals(2, activeAgents.size());
        
        // Verify all agents belong to active state and have state loaded
        activeAgents.forEach(agent -> {
            assertNotNull(agent.getState());
            assertEquals(activeState.getId(), agent.getState().getId());
            assertEquals("ACTIVE", agent.getState().getState());
        });

        assertTrue(activeAgents.stream().anyMatch(a -> a.getName().equals("Agent One")));
        assertTrue(activeAgents.stream().anyMatch(a -> a.getName().equals("Agent Two")));
    }

    @Test
    @DisplayName("Should find agents by inactive state id")
    void shouldFindAgentsByInactiveStateId() {
        // When
        List<Agent> inactiveAgents = agentRepository.findByStateIdWithState(inactiveState.getId());

        // Then
        assertNotNull(inactiveAgents);
        assertEquals(1, inactiveAgents.size());
        
        Agent agent = inactiveAgents.get(0);
        assertEquals("Inactive Agent", agent.getName());
        assertEquals(inactiveState.getId(), agent.getState().getId());
        assertEquals("INACTIVE", agent.getState().getState());
    }

    @Test
    @DisplayName("Should return empty list when no agents exist for state")
    void shouldReturnEmptyListWhenNoAgentsExistForState() {
        // When
        List<Agent> agents = agentRepository.findByStateIdWithState(eliminatedState.getId());

        // Then
        assertNotNull(agents);
        assertTrue(agents.isEmpty());
    }

    @Test
    @DisplayName("Should find agents by name containing (case insensitive) with state eagerly loaded")
    void shouldFindAgentsByNameContainingCaseInsensitiveWithStateEagerlyLoaded() {
        // When
        List<Agent> agents = agentRepository.findByNameContainingIgnoreCaseWithState("agent");

        // Then
        assertNotNull(agents);
        assertEquals(3, agents.size()); // All agents contain "agent" in name
        
        // Verify all have state loaded
        agents.forEach(agent -> {
            assertNotNull(agent.getState());
            assertNotNull(agent.getState().getState());
            assertTrue(agent.getName().toLowerCase().contains("agent"));
        });
    }

    @Test
    @DisplayName("Should find agents by partial name search")
    void shouldFindAgentsByPartialNameSearch() {
        // When
        List<Agent> agents = agentRepository.findByNameContainingIgnoreCaseWithState("ONE");

        // Then
        assertNotNull(agents);
        assertEquals(1, agents.size());
        
        Agent agent = agents.get(0);
        assertEquals("Agent One", agent.getName());
        assertNotNull(agent.getState());
    }

    @Test
    @DisplayName("Should handle case insensitive name search")
    void shouldHandleCaseInsensitiveNameSearch() {
        // When - search with different cases
        List<Agent> lowerCaseResult = agentRepository.findByNameContainingIgnoreCaseWithState("two");
        List<Agent> upperCaseResult = agentRepository.findByNameContainingIgnoreCaseWithState("TWO");
        List<Agent> mixedCaseResult = agentRepository.findByNameContainingIgnoreCaseWithState("TwO");

        // Then
        assertEquals(1, lowerCaseResult.size());
        assertEquals(1, upperCaseResult.size());
        assertEquals(1, mixedCaseResult.size());
        
        assertEquals("Agent Two", lowerCaseResult.get(0).getName());
        assertEquals("Agent Two", upperCaseResult.get(0).getName());
        assertEquals("Agent Two", mixedCaseResult.get(0).getName());
    }

    @Test
    @DisplayName("Should return empty list when no agents match name search")
    void shouldReturnEmptyListWhenNoAgentsMatchNameSearch() {
        // When
        List<Agent> agents = agentRepository.findByNameContainingIgnoreCaseWithState("NonExistent");

        // Then
        assertNotNull(agents);
        assertTrue(agents.isEmpty());
    }

    @Test
    @DisplayName("Should use backward compatibility methods correctly")
    void shouldUseBackwardCompatibilityMethodsCorrectly() {
        // When
        List<Agent> agentsByState = agentRepository.findByStateId(activeState.getId());
        List<Agent> agentsByName = agentRepository.findByNameContainingIgnoreCase("Agent");

        // Then
        assertNotNull(agentsByState);
        assertNotNull(agentsByName);
        
        assertEquals(2, agentsByState.size());
        assertEquals(3, agentsByName.size());
        
        // Note: These methods might not have state eagerly loaded, 
        // so we don't test for that to avoid LazyInitializationException
    }

    @Test
    @DisplayName("Should save and retrieve agent correctly")
    void shouldSaveAndRetrieveAgentCorrectly() {
        // Given
        Agent newAgent = TestDataFactory.createValidAgent(activeState);
        newAgent.setName("New Test Agent");
        newAgent.setDescription("New agent for testing save operation");
        newAgent.setLocation("Braga");
        newAgent.setRestrictions("No special restrictions");
        newAgent.setActivityFlow("Standard activity flow");

        // When
        Agent savedAgent = agentRepository.save(newAgent);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context to ensure fresh load

        Optional<Agent> retrievedAgent = agentRepository.findByIdWithState(savedAgent.getId());

        // Then
        assertTrue(retrievedAgent.isPresent());
        Agent agent = retrievedAgent.get();
        
        assertEquals(savedAgent.getId(), agent.getId());
        assertEquals("New Test Agent", agent.getName());
        assertEquals("New agent for testing save operation", agent.getDescription());
        assertEquals("Braga", agent.getLocation());
        assertEquals("No special restrictions", agent.getRestrictions());
        assertEquals("Standard activity flow", agent.getActivityFlow());
        assertEquals(activeState.getId(), agent.getState().getId());
        
        assertNotNull(agent.getCreatedAt());
        assertNotNull(agent.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update agent correctly")
    void shouldUpdateAgentCorrectly() {
        // Given
        Agent agentToUpdate = testAgent1;
        String originalName = agentToUpdate.getName();

        // When
        agentToUpdate.setName("Updated Agent Name");
        agentToUpdate.setDescription("Updated description");
        agentToUpdate.setLocation("Updated location");
        
        Agent updatedAgent = agentRepository.save(agentToUpdate);
        entityManager.flush();

        // Then
        assertEquals(testAgent1.getId(), updatedAgent.getId());
        assertEquals("Updated Agent Name", updatedAgent.getName());
        assertEquals("Updated description", updatedAgent.getDescription());
        assertEquals("Updated location", updatedAgent.getLocation());
        assertNotEquals(originalName, updatedAgent.getName());
    }

    @Test
    @DisplayName("Should delete agent correctly")
    void shouldDeleteAgentCorrectly() {
        // Given
        Long agentIdToDelete = testAgent1.getId();
        assertTrue(agentRepository.findById(agentIdToDelete).isPresent());

        // When
        agentRepository.delete(testAgent1);
        entityManager.flush();

        // Then
        assertFalse(agentRepository.findById(agentIdToDelete).isPresent());
        
        // Verify count decreased
        List<Agent> remainingAgents = agentRepository.findAllWithState();
        assertEquals(2, remainingAgents.size()); // Was 3, now should be 2
    }

    @Test
    @DisplayName("Should handle multiple agents with similar names")
    void shouldHandleMultipleAgentsWithSimilarNames() {
        // Given
        Agent similarAgent1 = TestDataFactory.createValidAgent(activeState);
        similarAgent1.setName("Test Agent Alpha");
        similarAgent1 = entityManager.persistAndFlush(similarAgent1);

        Agent similarAgent2 = TestDataFactory.createValidAgent(activeState);
        similarAgent2.setName("Test Agent Beta");
        similarAgent2 = entityManager.persistAndFlush(similarAgent2);

        Agent similarAgent3 = TestDataFactory.createValidAgent(activeState);
        similarAgent3.setName("Different Name");
        similarAgent3 = entityManager.persistAndFlush(similarAgent3);

        // When
        List<Agent> testAgents = agentRepository.findByNameContainingIgnoreCaseWithState("Test Agent");

        // Then
        assertNotNull(testAgents);
        assertEquals(2, testAgents.size()); // Only the two with "Test Agent" in name
        
        assertTrue(testAgents.stream().anyMatch(a -> a.getName().equals("Test Agent Alpha")));
        assertTrue(testAgents.stream().anyMatch(a -> a.getName().equals("Test Agent Beta")));
        assertFalse(testAgents.stream().anyMatch(a -> a.getName().equals("Different Name")));
    }

    @Test
    @DisplayName("Should maintain referential integrity with state")
    void shouldMaintainReferentialIntegrityWithState() {
        // Given
        Agent agent = TestDataFactory.createValidAgent(activeState);
        agent.setName("Integrity Test Agent");
        agent = entityManager.persistAndFlush(agent);

        // When
        Optional<Agent> retrievedAgent = agentRepository.findByIdWithState(agent.getId());

        // Then
        assertTrue(retrievedAgent.isPresent());
        Agent foundAgent = retrievedAgent.get();
        
        assertNotNull(foundAgent.getState());
        assertEquals(activeState.getId(), foundAgent.getState().getId());
        assertEquals(activeState.getState(), foundAgent.getState().getState());
    }

    @Test
    @DisplayName("Should handle empty string searches correctly")
    void shouldHandleEmptyStringSearchesCorrectly() {
        // When
        List<Agent> emptyStringResult = agentRepository.findByNameContainingIgnoreCaseWithState("");

        // Then
        assertNotNull(emptyStringResult);
        assertEquals(3, emptyStringResult.size()); // Empty string should match all
    }

    @Test
    @DisplayName("Should handle null and edge cases in search")
    void shouldHandleNullAndEdgeCasesInSearch() {
        // When & Then - These should not throw exceptions
        List<Agent> spaceResult = agentRepository.findByNameContainingIgnoreCaseWithState(" ");
        List<Agent> tabResult = agentRepository.findByNameContainingIgnoreCaseWithState("\t");
        List<Agent> newlineResult = agentRepository.findByNameContainingIgnoreCaseWithState("\n");

        assertNotNull(spaceResult);
        assertNotNull(tabResult);
        assertNotNull(newlineResult);
        
        // These searches should return empty since no agent names contain these characters
        assertTrue(spaceResult.isEmpty() || spaceResult.stream().allMatch(a -> a.getName().contains(" ")));
        assertTrue(tabResult.isEmpty());
        assertTrue(newlineResult.isEmpty());
    }
}