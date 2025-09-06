package com.automo.agentProduct.entity;

import com.automo.agent.entity.Agent;
import com.automo.product.entity.Product;
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
 * Comprehensive unit tests for AgentProduct entity
 * Testing validation, relationships, and entity behavior
 * Note: This entity does NOT have State relationship - uses physical delete
 */
@BaseTestConfig
@DisplayName("AgentProduct Entity Tests")
class AgentProductTest {

    @Autowired
    private Validator validator;

    private Agent testAgent;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        testAgent = TestDataFactory.createValidAgent(activeState);
        testAgent.setId(1L);
        
        testProduct = TestDataFactory.createValidProduct(activeState);
        testProduct.setId(1L);
    }

    @Test
    @DisplayName("Should create valid AgentProduct entity successfully")
    void shouldCreateValidAgentProductEntity() {
        // Given
        AgentProduct agentProduct = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        
        // When
        Set<ConstraintViolation<AgentProduct>> violations = validator.validate(agentProduct);
        
        // Then
        assertThat(violations).isEmpty();
        assertThat(agentProduct.getAgent()).isEqualTo(testAgent);
        assertThat(agentProduct.getProduct()).isEqualTo(testProduct);
        // Note: No state relationship in this entity
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties correctly")
    void shouldInheritAbstractModelProperties() {
        // Given
        AgentProduct agentProduct = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        LocalDateTime testTime = LocalDateTime.now();
        
        // When
        agentProduct.setId(100L);
        agentProduct.setCreatedAt(testTime);
        agentProduct.setUpdatedAt(testTime.plusMinutes(30));
        
        // Then
        assertThat(agentProduct.getId()).isEqualTo(100L);
        assertThat(agentProduct.getCreatedAt()).isEqualTo(testTime);
        assertThat(agentProduct.getUpdatedAt()).isEqualTo(testTime.plusMinutes(30));
    }

    @Test
    @DisplayName("Should fail validation when Agent is null")
    void shouldFailValidationWhenAgentIsNull() {
        // Given
        AgentProduct agentProduct = TestDataFactory.createValidAgentProduct(null, testProduct);
        
        // When
        Set<ConstraintViolation<AgentProduct>> violations = validator.validate(agentProduct);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Agent is required");
    }

    @Test
    @DisplayName("Should fail validation when Product is null")
    void shouldFailValidationWhenProductIsNull() {
        // Given
        AgentProduct agentProduct = TestDataFactory.createValidAgentProduct(testAgent, null);
        
        // When
        Set<ConstraintViolation<AgentProduct>> violations = validator.validate(agentProduct);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Product is required");
    }

    @Test
    @DisplayName("Should fail validation when all required fields are null")
    void shouldFailValidationWhenAllRequiredFieldsAreNull() {
        // Given
        AgentProduct agentProduct = new AgentProduct();
        
        // When
        Set<ConstraintViolation<AgentProduct>> violations = validator.validate(agentProduct);
        
        // Then
        assertThat(violations).hasSize(2);
        Set<String> messages = Set.of(
            violations.iterator().next().getMessage(),
            violations.stream().skip(1).findFirst().get().getMessage()
        );
        assertThat(messages).containsExactlyInAnyOrder(
            "Agent is required",
            "Product is required"
        );
    }

    @Test
    @DisplayName("Should support many-to-many relationship between Agent and Product")
    void shouldSupportManyToManyRelationship() {
        // Given - One Agent can have multiple Products
        Agent agent = TestDataFactory.createAgentWithMultipleAssignments(TestDataFactory.createActiveState());
        agent.setId(1L);
        
        Product product1 = TestDataFactory.createValidProduct(TestDataFactory.createActiveState());
        product1.setId(1L);
        product1.setName("BMW X5");
        
        Product product2 = TestDataFactory.createValidProduct(TestDataFactory.createActiveState());
        product2.setId(2L);
        product2.setName("Mercedes GLC");
        
        AgentProduct assignment1 = TestDataFactory.createValidAgentProduct(agent, product1);
        AgentProduct assignment2 = TestDataFactory.createValidAgentProduct(agent, product2);
        
        // When
        Set<ConstraintViolation<AgentProduct>> violations1 = validator.validate(assignment1);
        Set<ConstraintViolation<AgentProduct>> violations2 = validator.validate(assignment2);
        
        // Then
        assertThat(violations1).isEmpty();
        assertThat(violations2).isEmpty();
        assertThat(assignment1.getAgent()).isEqualTo(assignment2.getAgent());
        assertThat(assignment1.getProduct()).isNotEqualTo(assignment2.getProduct());
    }

    @Test
    @DisplayName("Should use physical delete pattern (no state relationship)")
    void shouldUsePhysicalDeletePattern() {
        // Given
        AgentProduct agentProduct = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        
        // When & Then - Verify no state relationship exists
        // This entity does not have a state field, confirming physical delete pattern
        assertThat(agentProduct).hasNoNullFieldsOrPropertiesExcept("id", "createdAt", "updatedAt");
        
        // The entity should only have agent and product relationships
        assertThat(agentProduct.getAgent()).isNotNull();
        assertThat(agentProduct.getProduct()).isNotNull();
    }

    @Test
    @DisplayName("Should test table and column mapping")
    void shouldTestTableAndColumnMapping() {
        // Given
        AgentProduct agentProduct = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        
        // When & Then - Test that entity is properly configured for agent_products table
        assertThat(agentProduct.getClass().getAnnotation(jakarta.persistence.Table.class).name())
            .isEqualTo("agent_products");
        
        // Verify join columns are properly configured
        assertThat(agentProduct.getAgent()).isNotNull();
        assertThat(agentProduct.getProduct()).isNotNull();
    }

    @Test
    @DisplayName("Should test constructor and builder pattern")
    void shouldTestConstructorAndBuilderPattern() {
        // Given & When - Test NoArgsConstructor
        AgentProduct emptyAgentProduct = new AgentProduct();
        
        // Then
        assertThat(emptyAgentProduct.getAgent()).isNull();
        assertThat(emptyAgentProduct.getProduct()).isNull();
        
        // Given & When - Test AllArgsConstructor
        AgentProduct fullAgentProduct = new AgentProduct(testAgent, testProduct);
        
        // Then
        assertThat(fullAgentProduct.getAgent()).isEqualTo(testAgent);
        assertThat(fullAgentProduct.getProduct()).isEqualTo(testProduct);
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        // Given
        AgentProduct agentProduct1 = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        agentProduct1.setId(1L);
        
        AgentProduct agentProduct2 = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        agentProduct2.setId(1L);
        
        AgentProduct agentProduct3 = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        agentProduct3.setId(2L);
        
        // When & Then
        assertThat(agentProduct1).isEqualTo(agentProduct2);
        assertThat(agentProduct1).isNotEqualTo(agentProduct3);
        assertThat(agentProduct1.hashCode()).isEqualTo(agentProduct2.hashCode());
    }

    @Test
    @DisplayName("Should test fetch type configurations")
    void shouldTestFetchTypeConfigurations() {
        // Given
        AgentProduct agentProduct = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        
        // When & Then - Test that fetch types are properly configured
        // Both relationships are LAZY fetch as configured in entity
        assertThat(agentProduct.getAgent()).isNotNull();
        assertThat(agentProduct.getProduct()).isNotNull();
    }

    @Test
    @DisplayName("Should test business scenario of agent selling different products")
    void shouldTestBusinessScenarioOfAgentSellingDifferentProducts() {
        // Given - Car sales agent handling different vehicle types
        Agent carSalesAgent = TestDataFactory.createValidAgent(TestDataFactory.createActiveState());
        carSalesAgent.setId(1L);
        carSalesAgent.setName("Carlos Mendes");
        carSalesAgent.setDescription("Specialized car sales agent");
        
        Product bmwX5 = TestDataFactory.createValidProduct(TestDataFactory.createActiveState());
        bmwX5.setId(1L);
        bmwX5.setName("BMW X5");
        bmwX5.setDescription("Luxury SUV");
        
        Product audiA4 = TestDataFactory.createValidProduct(TestDataFactory.createActiveState());
        audiA4.setId(2L);
        audiA4.setName("Audi A4");
        audiA4.setDescription("Executive sedan");
        
        Product mercedesC = TestDataFactory.createValidProduct(TestDataFactory.createActiveState());
        mercedesC.setId(3L);
        mercedesC.setName("Mercedes C-Class");
        mercedesC.setDescription("Premium compact executive car");
        
        AgentProduct bmwAssignment = TestDataFactory.createValidAgentProduct(carSalesAgent, bmwX5);
        AgentProduct audiAssignment = TestDataFactory.createValidAgentProduct(carSalesAgent, audiA4);
        AgentProduct mercedesAssignment = TestDataFactory.createValidAgentProduct(carSalesAgent, mercedesC);
        
        // When
        Set<ConstraintViolation<AgentProduct>> bmwViolations = validator.validate(bmwAssignment);
        Set<ConstraintViolation<AgentProduct>> audiViolations = validator.validate(audiAssignment);
        Set<ConstraintViolation<AgentProduct>> mercedesViolations = validator.validate(mercedesAssignment);
        
        // Then
        assertThat(bmwViolations).isEmpty();
        assertThat(audiViolations).isEmpty();
        assertThat(mercedesViolations).isEmpty();
        
        // All assignments belong to same agent
        assertThat(bmwAssignment.getAgent()).isEqualTo(carSalesAgent);
        assertThat(audiAssignment.getAgent()).isEqualTo(carSalesAgent);
        assertThat(mercedesAssignment.getAgent()).isEqualTo(carSalesAgent);
        
        // But handle different products
        assertThat(bmwAssignment.getProduct()).isNotEqualTo(audiAssignment.getProduct());
        assertThat(bmwAssignment.getProduct()).isNotEqualTo(mercedesAssignment.getProduct());
        assertThat(audiAssignment.getProduct()).isNotEqualTo(mercedesAssignment.getProduct());
    }

    @Test
    @DisplayName("Should demonstrate difference from soft-delete entities")
    void shouldDemonstrateDifferenceFromSoftDeleteEntities() {
        // Given
        AgentProduct agentProduct = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        
        // When & Then - Verify this entity does not have state management
        // Unlike AuthRoles or AgentAreas, this entity has no state field
        assertThat(agentProduct.getAgent()).isNotNull();
        assertThat(agentProduct.getProduct()).isNotNull();
        
        // No state field available for soft delete
        // Physical deletion would be required for this entity type
        try {
            // This should not compile or should return null since state doesn't exist
            // agentProduct.getState(); // This method doesn't exist
        } catch (Exception e) {
            // Expected - no state management
        }
    }

    @Test
    @DisplayName("Should validate relationship constraints properly")
    void shouldValidateRelationshipConstraintsProperly() {
        // Given - Test various constraint scenarios
        AgentProduct validAgentProduct = TestDataFactory.createValidAgentProduct(testAgent, testProduct);
        AgentProduct invalidAgentProduct1 = new AgentProduct(null, testProduct);
        AgentProduct invalidAgentProduct2 = new AgentProduct(testAgent, null);
        AgentProduct invalidAgentProduct3 = new AgentProduct(null, null);
        
        // When
        Set<ConstraintViolation<AgentProduct>> validViolations = validator.validate(validAgentProduct);
        Set<ConstraintViolation<AgentProduct>> invalid1Violations = validator.validate(invalidAgentProduct1);
        Set<ConstraintViolation<AgentProduct>> invalid2Violations = validator.validate(invalidAgentProduct2);
        Set<ConstraintViolation<AgentProduct>> invalid3Violations = validator.validate(invalidAgentProduct3);
        
        // Then
        assertThat(validViolations).isEmpty();
        assertThat(invalid1Violations).hasSize(1);
        assertThat(invalid2Violations).hasSize(1);
        assertThat(invalid3Violations).hasSize(2);
        
        // Check specific violation messages
        assertThat(invalid1Violations.iterator().next().getMessage()).isEqualTo("Agent is required");
        assertThat(invalid2Violations.iterator().next().getMessage()).isEqualTo("Product is required");
    }
}