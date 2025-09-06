package com.automo.state.entity;

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
@DisplayName("Tests for State Entity")
class StateTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid State entity")
    void shouldCreateValidStateEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("ACTIVE", state.getState());
        assertNull(state.getDescription());
    }

    @Test
    @DisplayName("Should create valid State with description")
    void shouldCreateValidStateWithDescription() {
        // Given
        State state = new State();
        state.setState("ACTIVE");
        state.setDescription("Active state description");
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("ACTIVE", state.getState());
        assertEquals("Active state description", state.getDescription());
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        State state = new State();
        state.setState(null);
        state.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("state") && 
            v.getMessage().contains("State is required")));
    }

    @Test
    @DisplayName("Should fail validation with blank state")
    void shouldFailValidationWithBlankState() {
        // Given
        State state = new State();
        state.setState("");
        state.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("state") && 
            v.getMessage().contains("State is required")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace-only state")
    void shouldFailValidationWithWhitespaceOnlyState() {
        // Given
        State state = new State();
        state.setState("   ");
        state.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("state") && 
            v.getMessage().contains("State is required")));
    }

    @Test
    @DisplayName("Should create State with null description")
    void shouldCreateStateWithNullDescription() {
        // Given
        State state = new State();
        state.setState("INACTIVE");
        state.setDescription(null);
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("INACTIVE", state.getState());
        assertNull(state.getDescription());
    }

    @Test
    @DisplayName("Should create State with empty description")
    void shouldCreateStateWithEmptyDescription() {
        // Given
        State state = new State();
        state.setState("PENDING");
        state.setDescription("");
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("PENDING", state.getState());
        assertEquals("", state.getDescription());
    }

    @Test
    @DisplayName("Should create different system states")
    void shouldCreateDifferentSystemStates() {
        // Given
        String[] validStates = {
            "ACTIVE",
            "INACTIVE", 
            "PENDING",
            "ELIMINATED",
            "UNREAD",
            "READ",
            "PENDING PAYMENT",
            "PAYMENT IN ANALYSIS",
            "APPROVED PAYMENT"
        };
        
        String[] descriptions = {
            "Active state",
            "Inactive state",
            "Pending state",
            "Eliminated/soft deleted state",
            "Unread notification state",
            "Read notification state",
            "Pending payment state",
            "Payment under analysis state",
            "Payment approved state"
        };
        
        // When & Then
        for (int i = 0; i < validStates.length; i++) {
            State state = new State();
            state.setState(validStates[i]);
            state.setDescription(descriptions[i]);
            
            Set<ConstraintViolation<State>> violations = validator.validate(state);
            
            assertTrue(violations.isEmpty(), "State " + validStates[i] + " should be valid");
            assertEquals(validStates[i], state.getState());
            assertEquals(descriptions[i], state.getDescription());
        }
    }

    @Test
    @DisplayName("Should handle long state names")
    void shouldHandleLongStateNames() {
        // Given
        String longState = "VERY_LONG_STATE_NAME_THAT_MIGHT_BE_USED_FOR_SPECIAL_BUSINESS_CONDITIONS";
        String longDescription = "This is a very long description that might be used to describe " +
                                "a complex state that requires detailed explanation about " +
                                "its purpose and usage in various business contexts and workflows.";
        
        State state = new State();
        state.setState(longState);
        state.setDescription(longDescription);
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longState, state.getState());
        assertEquals(longDescription, state.getDescription());
    }

    @Test
    @DisplayName("Should handle special characters in state and description")
    void shouldHandleSpecialCharactersInStateAndDescription() {
        // Given
        String stateWithSpecialChars = "PENDING-APPROVAL_2024";
        String descriptionWithSpecialChars = "Pending Approval (2024) - Special Case & Additional Info!";
        
        State state = new State();
        state.setState(stateWithSpecialChars);
        state.setDescription(descriptionWithSpecialChars);
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(stateWithSpecialChars, state.getState());
        assertEquals(descriptionWithSpecialChars, state.getDescription());
    }

    @Test
    @DisplayName("Should handle numeric characters in state")
    void shouldHandleNumericCharactersInState() {
        // Given
        String numericState = "STATE123";
        String description = "Numeric state identifier";
        
        State state = new State();
        state.setState(numericState);
        state.setDescription(description);
        
        // When
        Set<ConstraintViolation<State>> violations = validator.validate(state);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(numericState, state.getState());
        assertEquals(description, state.getDescription());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state1 = TestDataFactory.createActiveState();
        State state2 = TestDataFactory.createActiveState();
        state1.setId(1L);
        state2.setId(1L);
        
        // Then
        assertEquals(state1, state2);
        assertEquals(state1.hashCode(), state2.hashCode());
        
        // When different IDs
        state2.setId(2L);
        
        // Then
        assertNotEquals(state1, state2);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties")
    void shouldInheritAbstractModelProperties() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        // When
        state.setId(1L);
        
        // Then
        assertNotNull(state.getId());
        assertEquals(1L, state.getId());
        // Note: createdAt and updatedAt are set by JPA auditing in real scenarios
    }

    @Test
    @DisplayName("Should support case-sensitive state names")
    void shouldSupportCaseSensitiveStateNames() {
        // Given
        State upperCase = new State();
        upperCase.setState("ACTIVE");
        upperCase.setDescription("Upper case");
        
        State lowerCase = new State();
        lowerCase.setState("active");
        lowerCase.setDescription("Lower case");
        
        State mixedCase = new State();
        mixedCase.setState("Active");
        mixedCase.setDescription("Mixed case");
        
        // When
        Set<ConstraintViolation<State>> violations1 = validator.validate(upperCase);
        Set<ConstraintViolation<State>> violations2 = validator.validate(lowerCase);
        Set<ConstraintViolation<State>> violations3 = validator.validate(mixedCase);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertTrue(violations3.isEmpty());
        
        assertEquals("ACTIVE", upperCase.getState());
        assertEquals("active", lowerCase.getState());
        assertEquals("Active", mixedCase.getState());
    }

    @Test
    @DisplayName("Should validate minimum requirements for business logic")
    void shouldValidateMinimumRequirementsForBusinessLogic() {
        // Given - Common states used in business
        String[] businessStates = {"ACTIVE", "INACTIVE", "PENDING", "ELIMINATED"};
        
        // When & Then
        for (String stateName : businessStates) {
            State state = new State();
            state.setState(stateName);
            state.setDescription(stateName + " Description");
            
            Set<ConstraintViolation<State>> violations = validator.validate(state);
            
            assertTrue(violations.isEmpty(), "Business state " + stateName + " should be valid");
            assertEquals(stateName, state.getState());
        }
    }

    @Test
    @DisplayName("Should handle constructor variations")
    void shouldHandleConstructorVariations() {
        // Given & When - Default constructor
        State defaultConstructor = new State();
        defaultConstructor.setState("TEST");
        defaultConstructor.setDescription("Test Description");
        
        // Given & When - All args constructor
        State allArgsConstructor = new State("TEST", "Test Description");
        
        // Then
        Set<ConstraintViolation<State>> violations1 = validator.validate(defaultConstructor);
        Set<ConstraintViolation<State>> violations2 = validator.validate(allArgsConstructor);
        
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        
        assertEquals("TEST", defaultConstructor.getState());
        assertEquals("Test Description", defaultConstructor.getDescription());
        assertEquals("TEST", allArgsConstructor.getState());
        assertEquals("Test Description", allArgsConstructor.getDescription());
    }

    @Test
    @DisplayName("Should maintain immutability expectations for state")
    void shouldMaintainImmutabilityExpectationsForState() {
        // Given
        State state = TestDataFactory.createActiveState();
        String originalState = state.getState();
        String originalDescription = state.getDescription();
        
        // When - modifying the returned strings shouldn't affect the entity
        String modifiedState = originalState.toLowerCase();
        
        // Then
        assertNotEquals(modifiedState, state.getState());
        assertEquals("ACTIVE", state.getState());
        assertNull(state.getDescription()); // Original doesn't have description
    }

    @Test
    @DisplayName("Should create standard system states")
    void shouldCreateStandardSystemStates() {
        // Given & When
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        State eliminatedState = TestDataFactory.createEliminatedState();
        
        // Then
        Set<ConstraintViolation<State>> violations1 = validator.validate(activeState);
        Set<ConstraintViolation<State>> violations2 = validator.validate(inactiveState);
        Set<ConstraintViolation<State>> violations3 = validator.validate(eliminatedState);
        
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertTrue(violations3.isEmpty());
        
        assertEquals("ACTIVE", activeState.getState());
        assertEquals("INACTIVE", inactiveState.getState());
        assertEquals("ELIMINATED", eliminatedState.getState());
    }
}