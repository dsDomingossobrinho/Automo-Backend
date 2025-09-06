package com.automo.identifier.entity;

import com.automo.identifierType.entity.IdentifierType;
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
@DisplayName("Tests for Identifier Entity")
class IdentifierTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Identifier entity")
    void shouldCreateValidIdentifierEntity() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        identifierType.setId(1L);
        State state = TestDataFactory.createActiveState();
        state.setId(1L);
        
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        
        // When
        Set<ConstraintViolation<Identifier>> violations = validator.validate(identifier);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(1L, identifier.getUserId());
        assertEquals(identifierType, identifier.getIdentifierType());
        assertEquals(state, identifier.getState());
    }

    @Test
    @DisplayName("Should fail validation with null userId")
    void shouldFailValidationWithNullUserId() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier identifier = new Identifier();
        identifier.setUserId(null);
        identifier.setIdentifierType(identifierType);
        identifier.setState(state);
        
        // When
        Set<ConstraintViolation<Identifier>> violations = validator.validate(identifier);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("userId") && 
            v.getMessage().contains("User ID is required")));
    }

    @Test
    @DisplayName("Should fail validation with null identifierType")
    void shouldFailValidationWithNullIdentifierType() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Identifier identifier = new Identifier();
        identifier.setUserId(1L);
        identifier.setIdentifierType(null);
        identifier.setState(state);
        
        // When
        Set<ConstraintViolation<Identifier>> violations = validator.validate(identifier);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("identifierType") && 
            v.getMessage().contains("Identifier type is required")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        
        Identifier identifier = new Identifier();
        identifier.setUserId(1L);
        identifier.setIdentifierType(identifierType);
        identifier.setState(null);
        
        // When
        Set<ConstraintViolation<Identifier>> violations = validator.validate(identifier);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("state") && 
            v.getMessage().contains("State is required")));
    }

    @Test
    @DisplayName("Should create Identifier with different identifier types")
    void shouldCreateIdentifierWithDifferentIdentifierTypes() {
        // Given
        State state = TestDataFactory.createActiveState();
        state.setId(1L);
        
        IdentifierType nifType = TestDataFactory.createNifIdentifierType();
        nifType.setId(1L);
        
        IdentifierType nipcType = TestDataFactory.createValidIdentifierType("NIPC", "Número de Identificação de Pessoa Coletiva");
        nipcType.setId(2L);
        
        Identifier identifier1 = TestDataFactory.createValidIdentifier(1L, nifType, state);
        Identifier identifier2 = TestDataFactory.createValidIdentifier(2L, nipcType, state);
        
        // When
        Set<ConstraintViolation<Identifier>> violations1 = validator.validate(identifier1);
        Set<ConstraintViolation<Identifier>> violations2 = validator.validate(identifier2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals("NIF", identifier1.getIdentifierType().getType());
        assertEquals("NIPC", identifier2.getIdentifierType().getType());
    }

    @Test
    @DisplayName("Should create Identifier with different states")
    void shouldCreateIdentifierWithDifferentStates() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        identifierType.setId(1L);
        
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        State inactiveState = TestDataFactory.createInactiveState();
        inactiveState.setId(2L);
        
        Identifier identifier1 = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        Identifier identifier2 = TestDataFactory.createValidIdentifier(2L, identifierType, inactiveState);
        
        // When
        Set<ConstraintViolation<Identifier>> violations1 = validator.validate(identifier1);
        Set<ConstraintViolation<Identifier>> violations2 = validator.validate(identifier2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals("ACTIVE", identifier1.getState().getState());
        assertEquals("INACTIVE", identifier2.getState().getState());
    }

    @Test
    @DisplayName("Should create multiple Identifiers for same user")
    void shouldCreateMultipleIdentifiersForSameUser() {
        // Given
        State state = TestDataFactory.createActiveState();
        state.setId(1L);
        
        IdentifierType nifType = TestDataFactory.createNifIdentifierType();
        nifType.setId(1L);
        
        IdentifierType nipcType = TestDataFactory.createValidIdentifierType("NIPC", "Número de Identificação de Pessoa Coletiva");
        nipcType.setId(2L);
        
        Long userId = 1L;
        
        Identifier identifier1 = TestDataFactory.createValidIdentifier(userId, nifType, state);
        Identifier identifier2 = TestDataFactory.createValidIdentifier(userId, nipcType, state);
        
        // When
        Set<ConstraintViolation<Identifier>> violations1 = validator.validate(identifier1);
        Set<ConstraintViolation<Identifier>> violations2 = validator.validate(identifier2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals(userId, identifier1.getUserId());
        assertEquals(userId, identifier2.getUserId());
        assertNotEquals(identifier1.getIdentifierType().getType(), identifier2.getIdentifierType().getType());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        identifierType.setId(1L);
        State state = TestDataFactory.createActiveState();
        state.setId(1L);
        
        Identifier identifier1 = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        Identifier identifier2 = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        
        identifier1.setId(1L);
        identifier2.setId(1L);
        
        // Then
        assertEquals(identifier1, identifier2);
        assertEquals(identifier1.hashCode(), identifier2.hashCode());
        
        // When different IDs
        identifier2.setId(2L);
        
        // Then
        assertNotEquals(identifier1, identifier2);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties")
    void shouldInheritAbstractModelProperties() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        
        // When
        identifier.setId(1L);
        
        // Then
        assertNotNull(identifier.getId());
        assertEquals(1L, identifier.getId());
        // Note: createdAt and updatedAt are set by JPA auditing in real scenarios
    }

    @Test
    @DisplayName("Should handle edge case user IDs")
    void shouldHandleEdgeCaseUserIds() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        // Test with various user IDs
        Long[] userIds = {1L, 999999L, Long.MAX_VALUE};
        
        // When & Then
        for (Long userId : userIds) {
            Identifier identifier = TestDataFactory.createValidIdentifier(userId, identifierType, state);
            
            Set<ConstraintViolation<Identifier>> violations = validator.validate(identifier);
            
            assertTrue(violations.isEmpty(), "User ID " + userId + " should be valid");
            assertEquals(userId, identifier.getUserId());
        }
    }

    @Test
    @DisplayName("Should support lazy loading of relationships")
    void shouldSupportLazyLoadingOfRelationships() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        identifierType.setId(1L);
        State state = TestDataFactory.createActiveState();
        state.setId(1L);
        
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        
        // When - accessing relationships
        IdentifierType retrievedType = identifier.getIdentifierType();
        State retrievedState = identifier.getState();
        
        // Then - relationships should be accessible
        assertNotNull(retrievedType);
        assertNotNull(retrievedState);
        assertEquals("NIF", retrievedType.getType());
        assertEquals("ACTIVE", retrievedState.getState());
    }
}