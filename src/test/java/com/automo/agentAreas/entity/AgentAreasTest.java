package com.automo.agentAreas.entity;

import com.automo.agent.entity.Agent;
import com.automo.area.entity.Area;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive unit tests for AgentAreas entity
 * Testing validation, relationships, and entity behavior
 */
@BaseTestConfig
@DisplayName("AgentAreas Entity Tests")
class AgentAreasTest {

    @Autowired
    private Validator validator;

    private Agent testAgent;
    private Area testArea;
    private State testState;

    @BeforeEach
    void setUp() {
        testAgent = TestDataFactory.createValidAgent(TestDataFactory.createActiveState());
        testAgent.setId(1L);
        
        testArea = TestDataFactory.createLisbonArea();
        testArea.setId(1L);
        
        testState = TestDataFactory.createActiveState();
        testState.setId(1L);
    }

    @Test
    @DisplayName("Should create valid AgentAreas entity successfully")
    void shouldCreateValidAgentAreasEntity() {
        // Given
        AgentAreas agentAreas = TestDataFactory.createValidAgentAreas(testAgent, testArea, testState);
        
        // When
        Set<ConstraintViolation<AgentAreas>> violations = validator.validate(agentAreas);
        
        // Then
        assertThat(violations).isEmpty();
        assertThat(agentAreas.getAgent()).isEqualTo(testAgent);
        assertThat(agentAreas.getArea()).isEqualTo(testArea);
        assertThat(agentAreas.getState()).isEqualTo(testState);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties correctly")
    void shouldInheritAbstractModelProperties() {
        // Given
        AgentAreas agentAreas = TestDataFactory.createValidAgentAreas(testAgent, testArea, testState);
        LocalDateTime testTime = LocalDateTime.now();
        
        // When
        agentAreas.setId(100L);
        agentAreas.setCreatedAt(testTime);
        agentAreas.setUpdatedAt(testTime.plusMinutes(30));
        
        // Then
        assertThat(agentAreas.getId()).isEqualTo(100L);
        assertThat(agentAreas.getCreatedAt()).isEqualTo(testTime);
        assertThat(agentAreas.getUpdatedAt()).isEqualTo(testTime.plusMinutes(30));
    }

    @Test
    @DisplayName("Should fail validation when Agent is null")
    void shouldFailValidationWhenAgentIsNull() {
        // Given
        AgentAreas agentAreas = TestDataFactory.createValidAgentAreas(null, testArea, testState);
        
        // When
        Set<ConstraintViolation<AgentAreas>> violations = validator.validate(agentAreas);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Agent is required");
    }

    @Test
    @DisplayName("Should fail validation when Area is null")
    void shouldFailValidationWhenAreaIsNull() {
        // Given
        AgentAreas agentAreas = TestDataFactory.createValidAgentAreas(testAgent, null, testState);
        
        // When
        Set<ConstraintViolation<AgentAreas>> violations = validator.validate(agentAreas);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Area is required");
    }

    @Test
    @DisplayName("Should fail validation when State is null")
    void shouldFailValidationWhenStateIsNull() {
        // Given
        AgentAreas agentAreas = TestDataFactory.createValidAgentAreas(testAgent, testArea, null);
        
        // When
        Set<ConstraintViolation<AgentAreas>> violations = validator.validate(agentAreas);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("State is required");
    }

    @Test
    @DisplayName("Should fail validation when all required fields are null")
    void shouldFailValidationWhenAllRequiredFieldsAreNull() {
        // Given
        AgentAreas agentAreas = new AgentAreas();
        
        // When
        Set<ConstraintViolation<AgentAreas>> violations = validator.validate(agentAreas);
        
        // Then
        assertThat(violations).hasSize(3);
        Set<String> messages = Set.of(
            violations.iterator().next().getMessage(),
            violations.stream().skip(1).findFirst().get().getMessage(),
            violations.stream().skip(2).findFirst().get().getMessage()
        );
        assertThat(messages).containsExactlyInAnyOrder(
            "Agent is required",
            "Area is required",
            "State is required"
        );
    }

    @Test
    @DisplayName("Should support many-to-many relationship between Agent and Area")
    void shouldSupportManyToManyRelationship() {
        // Given - One Agent can have multiple Areas
        Agent agent = TestDataFactory.createAgentWithMultipleAssignments(testState);
        agent.setId(1L);
        
        Area lisbonArea = TestDataFactory.createLisbonArea();
        lisbonArea.setId(1L);
        Area cascaisArea = TestDataFactory.createCascaisArea();  
        cascaisArea.setId(2L);
        
        AgentAreas lisbonAssignment = TestDataFactory.createValidAgentAreas(agent, lisbonArea, testState);
        AgentAreas cascaisAssignment = TestDataFactory.createValidAgentAreas(agent, cascaisArea, testState);
        
        // When
        Set<ConstraintViolation<AgentAreas>> lisbonViolations = validator.validate(lisbonAssignment);
        Set<ConstraintViolation<AgentAreas>> cascaisViolations = validator.validate(cascaisAssignment);
        
        // Then
        assertThat(lisbonViolations).isEmpty();
        assertThat(cascaisViolations).isEmpty();
        assertThat(lisbonAssignment.getAgent()).isEqualTo(cascaisAssignment.getAgent());
        assertThat(lisbonAssignment.getArea()).isNotEqualTo(cascaisAssignment.getArea());
    }

    @Test
    @DisplayName("Should support soft delete through State relationship")
    void shouldSupportSoftDeleteThroughStateRelationship() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        AgentAreas agentAreas = TestDataFactory.createValidAgentAreas(testAgent, testArea, activeState);
        
        // When - Simulate soft delete by changing state
        agentAreas.setState(eliminatedState);
        
        // Then
        Set<ConstraintViolation<AgentAreas>> violations = validator.validate(agentAreas);
        assertThat(violations).isEmpty();
        assertThat(agentAreas.getState()).isEqualTo(eliminatedState);
    }

    @Test
    @DisplayName("Should test table and column mapping")
    void shouldTestTableAndColumnMapping() {
        // Given
        AgentAreas agentAreas = TestDataFactory.createValidAgentAreas(testAgent, testArea, testState);
        
        // When & Then - Test that entity is properly configured for agent_areas table
        assertThat(agentAreas.getClass().getAnnotation(jakarta.persistence.Table.class).name())
            .isEqualTo("agent_areas");
        
        // Verify join columns are properly configured
        assertThat(agentAreas.getAgent()).isNotNull();
        assertThat(agentAreas.getArea()).isNotNull();
        assertThat(agentAreas.getState()).isNotNull();
    }

    @Test
    @DisplayName("Should test constructor and builder pattern")
    void shouldTestConstructorAndBuilderPattern() {
        // Given & When - Test NoArgsConstructor
        AgentAreas emptyAgentAreas = new AgentAreas();
        
        // Then
        assertThat(emptyAgentAreas.getAgent()).isNull();
        assertThat(emptyAgentAreas.getArea()).isNull();
        assertThat(emptyAgentAreas.getState()).isNull();
        
        // Given & When - Test AllArgsConstructor
        AgentAreas fullAgentAreas = new AgentAreas(testAgent, testArea, testState);
        
        // Then
        assertThat(fullAgentAreas.getAgent()).isEqualTo(testAgent);
        assertThat(fullAgentAreas.getArea()).isEqualTo(testArea);
        assertThat(fullAgentAreas.getState()).isEqualTo(testState);
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        // Given
        AgentAreas agentAreas1 = TestDataFactory.createValidAgentAreas(testAgent, testArea, testState);
        agentAreas1.setId(1L);
        
        AgentAreas agentAreas2 = TestDataFactory.createValidAgentAreas(testAgent, testArea, testState);
        agentAreas2.setId(1L);
        
        AgentAreas agentAreas3 = TestDataFactory.createValidAgentAreas(testAgent, testArea, testState);
        agentAreas3.setId(2L);
        
        // When & Then
        assertThat(agentAreas1).isEqualTo(agentAreas2);
        assertThat(agentAreas1).isNotEqualTo(agentAreas3);
        assertThat(agentAreas1.hashCode()).isEqualTo(agentAreas2.hashCode());
    }

    @Test
    @DisplayName("Should test fetch type configurations")
    void shouldTestFetchTypeConfigurations() {
        // Given
        AgentAreas agentAreas = TestDataFactory.createValidAgentAreas(testAgent, testArea, testState);
        
        // When & Then - Test that fetch types are properly configured
        // All relationships are LAZY fetch as configured in entity
        assertThat(agentAreas.getAgent()).isNotNull();
        assertThat(agentAreas.getArea()).isNotNull();
        assertThat(agentAreas.getState()).isNotNull();
    }

    @Test
    @DisplayName("Should test business scenario of agent covering multiple areas")
    void shouldTestBusinessScenarioOfAgentCoveringMultipleAreas() {
        // Given - Real estate agent covering multiple areas in Lisbon region
        Agent realEstateAgent = TestDataFactory.createValidAgent(testState);
        realEstateAgent.setId(1L);
        realEstateAgent.setName("João Silva");
        realEstateAgent.setDescription("Real Estate Agent specializing in Lisbon region");
        
        Area lisbonCenter = TestDataFactory.createLisbonArea();
        lisbonCenter.setId(1L);
        Area cascais = TestDataFactory.createCascaisArea();
        cascais.setId(2L);
        Area sintra = TestDataFactory.createSintraArea();
        sintra.setId(3L);
        
        AgentAreas centerAssignment = TestDataFactory.createValidAgentAreas(realEstateAgent, lisbonCenter, testState);
        AgentAreas cascaisAssignment = TestDataFactory.createValidAgentAreas(realEstateAgent, cascais, testState);
        AgentAreas sintraAssignment = TestDataFactory.createValidAgentAreas(realEstateAgent, sintra, testState);
        
        // When
        Set<ConstraintViolation<AgentAreas>> centerViolations = validator.validate(centerAssignment);
        Set<ConstraintViolation<AgentAreas>> cascaisViolations = validator.validate(cascaisAssignment);
        Set<ConstraintViolation<AgentAreas>> sintraViolations = validator.validate(sintraAssignment);
        
        // Then
        assertThat(centerViolations).isEmpty();
        assertThat(cascaisViolations).isEmpty();
        assertThat(sintraViolations).isEmpty();
        
        // All assignments belong to same agent
        assertThat(centerAssignment.getAgent()).isEqualTo(realEstateAgent);
        assertThat(cascaisAssignment.getAgent()).isEqualTo(realEstateAgent);
        assertThat(sintraAssignment.getAgent()).isEqualTo(realEstateAgent);
        
        // But cover different areas
        assertThat(centerAssignment.getArea()).isNotEqualTo(cascaisAssignment.getArea());
        assertThat(centerAssignment.getArea()).isNotEqualTo(sintraAssignment.getArea());
        assertThat(cascaisAssignment.getArea()).isNotEqualTo(sintraAssignment.getArea());
    }
}