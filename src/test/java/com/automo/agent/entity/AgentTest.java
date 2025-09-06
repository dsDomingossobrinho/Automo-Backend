package com.automo.agent.entity;

import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Agent Entity")
class AgentTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Agent entity")
    void shouldCreateValidAgentEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        state.setId(1L);
        Agent agent = TestDataFactory.createValidAgent(state);
        
        // When
        Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Test Agent", agent.getName());
        assertEquals("Test agent description", agent.getDescription());
        assertEquals("Lisboa, Portugal", agent.getLocation());
        assertEquals("No restrictions", agent.getRestrictions());
        assertEquals("Standard flow", agent.getActivityFlow());
        assertEquals(state, agent.getState());
    }

    @Test
    @DisplayName("Should fail validation with null name")
    void shouldFailValidationWithNullName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Agent agent = new Agent();
        agent.setName(null);
        agent.setDescription("Test description");
        agent.setLocation("Lisboa, Portugal");
        agent.setRestrictions("No restrictions");
        agent.setActivityFlow("Standard flow");
        agent.setState(state);
        
        // When
        Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with blank name")
    void shouldFailValidationWithBlankName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Agent agent = new Agent();
        agent.setName("");
        agent.setDescription("Test description");
        agent.setLocation("Lisboa, Portugal");
        agent.setRestrictions("No restrictions");
        agent.setActivityFlow("Standard flow");
        agent.setState(state);
        
        // When
        Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace-only name")
    void shouldFailValidationWithWhitespaceOnlyName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Agent agent = new Agent();
        agent.setName("   ");
        agent.setDescription("Test description");
        agent.setLocation("Lisboa, Portugal");
        agent.setRestrictions("No restrictions");
        agent.setActivityFlow("Standard flow");
        agent.setState(state);
        
        // When
        Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Agent agent = new Agent();
        agent.setName("Test Agent");
        agent.setDescription("Test description");
        agent.setLocation("Lisboa, Portugal");
        agent.setRestrictions("No restrictions");
        agent.setActivityFlow("Standard flow");
        agent.setState(null);
        
        // When
        Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should create Agent with optional fields as null")
    void shouldCreateAgentWithOptionalFieldsAsNull() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Agent agent = new Agent();
        agent.setName("Test Agent");
        agent.setDescription(null); // Optional field
        agent.setLocation(null); // Optional field
        agent.setRestrictions(null); // Optional field
        agent.setActivityFlow(null); // Optional field
        agent.setState(state);
        
        // When
        Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Test Agent", agent.getName());
        assertNull(agent.getDescription());
        assertNull(agent.getLocation());
        assertNull(agent.getRestrictions());
        assertNull(agent.getActivityFlow());
        assertEquals(state, agent.getState());
    }

    @Test
    @DisplayName("Should create Agent with empty strings in optional fields")
    void shouldCreateAgentWithEmptyStringsInOptionalFields() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Agent agent = new Agent();
        agent.setName("Test Agent");
        agent.setDescription(""); // Empty but not null
        agent.setLocation(""); // Empty but not null
        agent.setRestrictions(""); // Empty but not null
        agent.setActivityFlow(""); // Empty but not null
        agent.setState(state);
        
        // When
        Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Test Agent", agent.getName());
        assertEquals("", agent.getDescription());
        assertEquals("", agent.getLocation());
        assertEquals("", agent.getRestrictions());
        assertEquals("", agent.getActivityFlow());
    }

    @Test
    @DisplayName("Should accept various valid names")
    void shouldAcceptVariousValidNames() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        String[] validNames = {
            "Agent Test",
            "João Silva",
            "Agent-123",
            "Agent_Test",
            "Agent José Maria dos Santos",
            "AI Assistant 3000",
            "Agent à la carte"
        };
        
        // When & Then
        for (String name : validNames) {
            Agent agent = new Agent();
            agent.setName(name);
            agent.setState(state);
            
            Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
            assertTrue(violations.isEmpty(), "Name '" + name + "' should be valid");
        }
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Agent agent1 = TestDataFactory.createValidAgent(state);
        Agent agent2 = TestDataFactory.createValidAgent(state);
        agent1.setId(1L);
        agent2.setId(1L);
        
        // Then
        assertEquals(agent1, agent2);
        assertEquals(agent1.hashCode(), agent2.hashCode());
        
        // When different IDs
        agent2.setId(2L);
        
        // Then
        assertNotEquals(agent1, agent2);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties")
    void shouldInheritAbstractModelProperties() {
        // Given
        State state = TestDataFactory.createActiveState();
        Agent agent = TestDataFactory.createValidAgent(state);
        
        // When
        agent.setId(1L);
        
        // Then
        assertNotNull(agent.getId());
        assertEquals(1L, agent.getId());
        // Note: createdAt and updatedAt are set by JPA auditing in real scenarios
    }

    @Test
    @DisplayName("Should validate agent with different states")
    void shouldValidateAgentWithDifferentStates() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        State eliminatedState = TestDataFactory.createEliminatedState();
        
        // When & Then - All states should be valid
        Agent agent1 = TestDataFactory.createValidAgent(activeState);
        Agent agent2 = TestDataFactory.createValidAgent(inactiveState);
        Agent agent3 = TestDataFactory.createValidAgent(eliminatedState);
        
        assertTrue(validator.validate(agent1).isEmpty());
        assertTrue(validator.validate(agent2).isEmpty());
        assertTrue(validator.validate(agent3).isEmpty());
        
        assertEquals(activeState, agent1.getState());
        assertEquals(inactiveState, agent2.getState());
        assertEquals(eliminatedState, agent3.getState());
    }

    @Test
    @DisplayName("Should create agent with long text values")
    void shouldCreateAgentWithLongTextValues() {
        // Given
        State state = TestDataFactory.createActiveState();
        String longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(10);
        
        Agent agent = new Agent();
        agent.setName("Test Agent");
        agent.setDescription(longText);
        agent.setLocation(longText);
        agent.setRestrictions(longText);
        agent.setActivityFlow(longText);
        agent.setState(state);
        
        // When
        Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longText, agent.getDescription());
        assertEquals(longText, agent.getLocation());
        assertEquals(longText, agent.getRestrictions());
        assertEquals(longText, agent.getActivityFlow());
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        // Given
        State state = TestDataFactory.createActiveState();
        state.setId(1L);
        
        Agent agent = TestDataFactory.createValidAgent(state);
        agent.setId(1L);
        
        // When
        String toString = agent.toString();
        
        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Agent"));
        // Note: The exact format depends on Lombok's @ToString implementation
    }

    @Test
    @DisplayName("Should handle special characters in text fields")
    void shouldHandleSpecialCharactersInTextFields() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Agent agent = new Agent();
        agent.setName("Agent João & María");
        agent.setDescription("Description with special chars: àáâãäåçèéêë");
        agent.setLocation("Lisboa - Rua da Conceição, 123 (2º andar)");
        agent.setRestrictions("Restrictions: < > & \" '");
        agent.setActivityFlow("Flow with unicode: ñ ü ß π ∑ ∆");
        agent.setState(state);
        
        // When
        Set<ConstraintViolation<Agent>> violations = validator.validate(agent);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Agent João & María", agent.getName());
        assertEquals("Description with special chars: àáâãäåçèéêë", agent.getDescription());
    }
}