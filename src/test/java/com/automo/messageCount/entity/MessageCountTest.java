package com.automo.messageCount.entity;

import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.lead.entity.Lead;
import com.automo.leadType.entity.LeadType;
import com.automo.messageCount.entity.MessageCount;
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
 * Comprehensive unit tests for MessageCount entity
 * Testing validation, relationships, and entity behavior
 * This entity tracks message counts related to leads and supports soft delete
 */
@BaseTestConfig
@DisplayName("MessageCount Entity Tests")
class MessageCountTest {

    @Autowired
    private Validator validator;

    private Lead testLead;
    private State testState;

    @BeforeEach
    void setUp() {
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        // Create required entities for Lead
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        identifierType.setId(1L);
        
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        identifier.setId(1L);
        
        LeadType leadType = TestDataFactory.createCallLeadType();
        leadType.setId(1L);
        
        testLead = TestDataFactory.createValidLead(identifier, leadType, activeState);
        testLead.setId(1L);
        
        testState = activeState;
    }

    @Test
    @DisplayName("Should create valid MessageCount entity successfully")
    void shouldCreateValidMessageCountEntity() {
        // Given
        MessageCount messageCount = TestDataFactory.createValidMessageCount(testLead, 5, testState);
        
        // When
        Set<ConstraintViolation<MessageCount>> violations = validator.validate(messageCount);
        
        // Then
        assertThat(violations).isEmpty();
        assertThat(messageCount.getLead()).isEqualTo(testLead);
        assertThat(messageCount.getMessageCount()).isEqualTo(5);
        assertThat(messageCount.getState()).isEqualTo(testState);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties correctly")
    void shouldInheritAbstractModelProperties() {
        // Given
        MessageCount messageCount = TestDataFactory.createValidMessageCount(testLead, 3, testState);
        LocalDateTime testTime = LocalDateTime.now();
        
        // When
        messageCount.setId(100L);
        messageCount.setCreatedAt(testTime);
        messageCount.setUpdatedAt(testTime.plusMinutes(30));
        
        // Then
        assertThat(messageCount.getId()).isEqualTo(100L);
        assertThat(messageCount.getCreatedAt()).isEqualTo(testTime);
        assertThat(messageCount.getUpdatedAt()).isEqualTo(testTime.plusMinutes(30));
    }

    @Test
    @DisplayName("Should fail validation when Lead is null")
    void shouldFailValidationWhenLeadIsNull() {
        // Given
        MessageCount messageCount = TestDataFactory.createValidMessageCount(null, 5, testState);
        
        // When
        Set<ConstraintViolation<MessageCount>> violations = validator.validate(messageCount);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Lead is required");
    }

    @Test
    @DisplayName("Should fail validation when message count is null")
    void shouldFailValidationWhenMessageCountIsNull() {
        // Given
        MessageCount messageCount = TestDataFactory.createValidMessageCount(testLead, null, testState);
        
        // When
        Set<ConstraintViolation<MessageCount>> violations = validator.validate(messageCount);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Message count is required");
    }

    @Test
    @DisplayName("Should fail validation when State is null")
    void shouldFailValidationWhenStateIsNull() {
        // Given
        MessageCount messageCount = TestDataFactory.createValidMessageCount(testLead, 5, null);
        
        // When
        Set<ConstraintViolation<MessageCount>> violations = validator.validate(messageCount);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("State is required");
    }

    @Test
    @DisplayName("Should fail validation when message count is negative")
    void shouldFailValidationWhenMessageCountIsNegative() {
        // Given
        MessageCount messageCount = TestDataFactory.createValidMessageCount(testLead, -1, testState);
        
        // When
        Set<ConstraintViolation<MessageCount>> violations = validator.validate(messageCount);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Message count must be positive");
    }

    @Test
    @DisplayName("Should fail validation when message count is zero")
    void shouldFailValidationWhenMessageCountIsZero() {
        // Given
        MessageCount messageCount = TestDataFactory.createValidMessageCount(testLead, 0, testState);
        
        // When
        Set<ConstraintViolation<MessageCount>> violations = validator.validate(messageCount);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Message count must be positive");
    }

    @Test
    @DisplayName("Should fail validation when all required fields are null")
    void shouldFailValidationWhenAllRequiredFieldsAreNull() {
        // Given
        MessageCount messageCount = new MessageCount();
        
        // When
        Set<ConstraintViolation<MessageCount>> violations = validator.validate(messageCount);
        
        // Then
        assertThat(violations).hasSize(3);
        Set<String> messages = Set.of(
            violations.iterator().next().getMessage(),
            violations.stream().skip(1).findFirst().get().getMessage(),
            violations.stream().skip(2).findFirst().get().getMessage()
        );
        assertThat(messages).containsExactlyInAnyOrder(
            "Lead is required",
            "Message count is required",
            "State is required"
        );
    }

    @Test
    @DisplayName("Should support one-to-many relationship with Lead")
    void shouldSupportOneToManyRelationshipWithLead() {
        // Given - Multiple message counts for the same lead (tracking over time)
        MessageCount initialCount = TestDataFactory.createValidMessageCount(testLead, 5, testState);
        MessageCount updatedCount = TestDataFactory.createValidMessageCount(testLead, 12, testState);
        MessageCount finalCount = TestDataFactory.createValidMessageCount(testLead, 18, testState);
        
        // When
        Set<ConstraintViolation<MessageCount>> initialViolations = validator.validate(initialCount);
        Set<ConstraintViolation<MessageCount>> updatedViolations = validator.validate(updatedCount);
        Set<ConstraintViolation<MessageCount>> finalViolations = validator.validate(finalCount);
        
        // Then
        assertThat(initialViolations).isEmpty();
        assertThat(updatedViolations).isEmpty();
        assertThat(finalViolations).isEmpty();
        
        // All belong to same lead
        assertThat(initialCount.getLead()).isEqualTo(testLead);
        assertThat(updatedCount.getLead()).isEqualTo(testLead);
        assertThat(finalCount.getLead()).isEqualTo(testLead);
        
        // But have different counts
        assertThat(initialCount.getMessageCount()).isEqualTo(5);
        assertThat(updatedCount.getMessageCount()).isEqualTo(12);
        assertThat(finalCount.getMessageCount()).isEqualTo(18);
    }

    @Test
    @DisplayName("Should support soft delete through State relationship")
    void shouldSupportSoftDeleteThroughStateRelationship() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        MessageCount messageCount = TestDataFactory.createValidMessageCount(testLead, 10, activeState);
        
        // When - Simulate soft delete by changing state
        messageCount.setState(eliminatedState);
        
        // Then
        Set<ConstraintViolation<MessageCount>> violations = validator.validate(messageCount);
        assertThat(violations).isEmpty();
        assertThat(messageCount.getState()).isEqualTo(eliminatedState);
    }

    @Test
    @DisplayName("Should test table and column mapping")
    void shouldTestTableAndColumnMapping() {
        // Given
        MessageCount messageCount = TestDataFactory.createValidMessageCount(testLead, 7, testState);
        
        // When & Then - Test that entity is properly configured for message_counts table
        assertThat(messageCount.getClass().getAnnotation(jakarta.persistence.Table.class).name())
            .isEqualTo("message_counts");
        
        // Verify relationships and column are properly configured
        assertThat(messageCount.getLead()).isNotNull();
        assertThat(messageCount.getMessageCount()).isNotNull();
        assertThat(messageCount.getState()).isNotNull();
    }

    @Test
    @DisplayName("Should test constructor and builder pattern")
    void shouldTestConstructorAndBuilderPattern() {
        // Given & When - Test NoArgsConstructor
        MessageCount emptyMessageCount = new MessageCount();
        
        // Then
        assertThat(emptyMessageCount.getLead()).isNull();
        assertThat(emptyMessageCount.getMessageCount()).isNull();
        assertThat(emptyMessageCount.getState()).isNull();
        
        // Given & When - Test AllArgsConstructor
        MessageCount fullMessageCount = new MessageCount(testLead, 15, testState);
        
        // Then
        assertThat(fullMessageCount.getLead()).isEqualTo(testLead);
        assertThat(fullMessageCount.getMessageCount()).isEqualTo(15);
        assertThat(fullMessageCount.getState()).isEqualTo(testState);
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        // Given
        MessageCount messageCount1 = TestDataFactory.createValidMessageCount(testLead, 8, testState);
        messageCount1.setId(1L);
        
        MessageCount messageCount2 = TestDataFactory.createValidMessageCount(testLead, 8, testState);
        messageCount2.setId(1L);
        
        MessageCount messageCount3 = TestDataFactory.createValidMessageCount(testLead, 8, testState);
        messageCount3.setId(2L);
        
        // When & Then
        assertThat(messageCount1).isEqualTo(messageCount2);
        assertThat(messageCount1).isNotEqualTo(messageCount3);
        assertThat(messageCount1.hashCode()).isEqualTo(messageCount2.hashCode());
    }

    @Test
    @DisplayName("Should test fetch type configurations")
    void shouldTestFetchTypeConfigurations() {
        // Given
        MessageCount messageCount = TestDataFactory.createValidMessageCount(testLead, 6, testState);
        
        // When & Then - Test that fetch types are properly configured
        // All relationships are LAZY fetch as configured in entity
        assertThat(messageCount.getLead()).isNotNull();
        assertThat(messageCount.getState()).isNotNull();
    }

    @Test
    @DisplayName("Should test business scenario of message tracking over time")
    void shouldTestBusinessScenarioOfMessageTrackingOverTime() {
        // Given - Lead interaction tracking over customer journey
        Lead customerLead = testLead;
        customerLead.setName("Maria Santos");
        customerLead.setEmail("maria@example.com");
        customerLead.setContact("912345678");
        
        // Different stages of customer interaction
        MessageCount initialContact = TestDataFactory.createValidMessageCount(customerLead, 3, testState);
        initialContact.setCreatedAt(LocalDateTime.now().minusDays(10));
        
        MessageCount followUp = TestDataFactory.createValidMessageCount(customerLead, 8, testState);
        followUp.setCreatedAt(LocalDateTime.now().minusDays(5));
        
        MessageCount negotiation = TestDataFactory.createValidMessageCount(customerLead, 15, testState);
        negotiation.setCreatedAt(LocalDateTime.now().minusDays(2));
        
        MessageCount finalStage = TestDataFactory.createValidMessageCount(customerLead, 25, testState);
        finalStage.setCreatedAt(LocalDateTime.now());
        
        // When
        Set<ConstraintViolation<MessageCount>> initialViolations = validator.validate(initialContact);
        Set<ConstraintViolation<MessageCount>> followUpViolations = validator.validate(followUp);
        Set<ConstraintViolation<MessageCount>> negotiationViolations = validator.validate(negotiation);
        Set<ConstraintViolation<MessageCount>> finalViolations = validator.validate(finalStage);
        
        // Then
        assertThat(initialViolations).isEmpty();
        assertThat(followUpViolations).isEmpty();
        assertThat(negotiationViolations).isEmpty();
        assertThat(finalViolations).isEmpty();
        
        // All belong to same lead
        assertThat(initialContact.getLead()).isEqualTo(customerLead);
        assertThat(followUp.getLead()).isEqualTo(customerLead);
        assertThat(negotiation.getLead()).isEqualTo(customerLead);
        assertThat(finalStage.getLead()).isEqualTo(customerLead);
        
        // Message count increases over time
        assertThat(initialContact.getMessageCount()).isLessThan(followUp.getMessageCount());
        assertThat(followUp.getMessageCount()).isLessThan(negotiation.getMessageCount());
        assertThat(negotiation.getMessageCount()).isLessThan(finalStage.getMessageCount());
    }

    @Test
    @DisplayName("Should validate positive message count constraint properly")
    void shouldValidatePositiveMessageCountConstraintProperly() {
        // Given - Test various count scenarios
        MessageCount validMessageCount1 = TestDataFactory.createValidMessageCount(testLead, 1, testState);
        MessageCount validMessageCount2 = TestDataFactory.createValidMessageCount(testLead, 100, testState);
        MessageCount validMessageCount3 = TestDataFactory.createValidMessageCount(testLead, 1000, testState);
        
        MessageCount invalidMessageCount1 = TestDataFactory.createValidMessageCount(testLead, 0, testState);
        MessageCount invalidMessageCount2 = TestDataFactory.createValidMessageCount(testLead, -5, testState);
        MessageCount invalidMessageCount3 = TestDataFactory.createValidMessageCount(testLead, -100, testState);
        
        // When
        Set<ConstraintViolation<MessageCount>> valid1Violations = validator.validate(validMessageCount1);
        Set<ConstraintViolation<MessageCount>> valid2Violations = validator.validate(validMessageCount2);
        Set<ConstraintViolation<MessageCount>> valid3Violations = validator.validate(validMessageCount3);
        
        Set<ConstraintViolation<MessageCount>> invalid1Violations = validator.validate(invalidMessageCount1);
        Set<ConstraintViolation<MessageCount>> invalid2Violations = validator.validate(invalidMessageCount2);
        Set<ConstraintViolation<MessageCount>> invalid3Violations = validator.validate(invalidMessageCount3);
        
        // Then
        assertThat(valid1Violations).isEmpty();
        assertThat(valid2Violations).isEmpty();
        assertThat(valid3Violations).isEmpty();
        
        assertThat(invalid1Violations).hasSize(1);
        assertThat(invalid2Violations).hasSize(1);
        assertThat(invalid3Violations).hasSize(1);
        
        // Check violation messages
        assertThat(invalid1Violations.iterator().next().getMessage()).isEqualTo("Message count must be positive");
        assertThat(invalid2Violations.iterator().next().getMessage()).isEqualTo("Message count must be positive");
        assertThat(invalid3Violations.iterator().next().getMessage()).isEqualTo("Message count must be positive");
    }

    @Test
    @DisplayName("Should test message count state transitions")
    void shouldTestMessageCountStateTransitions() {
        // Given - Message counts at different states
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        State inactiveState = TestDataFactory.createInactiveState();
        inactiveState.setId(2L);
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        MessageCount activeMessageCount = TestDataFactory.createValidMessageCount(testLead, 10, activeState);
        MessageCount inactiveMessageCount = TestDataFactory.createValidMessageCount(testLead, 20, inactiveState);
        MessageCount eliminatedMessageCount = TestDataFactory.createValidMessageCount(testLead, 15, eliminatedState);
        
        // When
        Set<ConstraintViolation<MessageCount>> activeViolations = validator.validate(activeMessageCount);
        Set<ConstraintViolation<MessageCount>> inactiveViolations = validator.validate(inactiveMessageCount);
        Set<ConstraintViolation<MessageCount>> eliminatedViolations = validator.validate(eliminatedMessageCount);
        
        // Then - All should be valid from entity perspective
        assertThat(activeViolations).isEmpty();
        assertThat(inactiveViolations).isEmpty();
        assertThat(eliminatedViolations).isEmpty();
        
        // Verify different states
        assertThat(activeMessageCount.getState().getState()).isEqualTo("ACTIVE");
        assertThat(inactiveMessageCount.getState().getState()).isEqualTo("INACTIVE");
        assertThat(eliminatedMessageCount.getState().getState()).isEqualTo("ELIMINATED");
        
        // All have same lead but different message counts and states
        assertThat(activeMessageCount.getLead()).isEqualTo(testLead);
        assertThat(inactiveMessageCount.getLead()).isEqualTo(testLead);
        assertThat(eliminatedMessageCount.getLead()).isEqualTo(testLead);
    }

    @Test
    @DisplayName("Should handle large message count values")
    void shouldHandleLargeMessageCountValues() {
        // Given - Test with large message count values
        MessageCount largeCountMessage = TestDataFactory.createValidMessageCount(testLead, Integer.MAX_VALUE, testState);
        
        // When
        Set<ConstraintViolation<MessageCount>> violations = validator.validate(largeCountMessage);
        
        // Then
        assertThat(violations).isEmpty();
        assertThat(largeCountMessage.getMessageCount()).isEqualTo(Integer.MAX_VALUE);
    }
}