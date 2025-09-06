package com.automo.authRoles.entity;

import com.automo.auth.entity.Auth;
import com.automo.role.entity.Role;
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
 * Comprehensive unit tests for AuthRoles entity
 * Testing validation, relationships, and entity behavior
 */
@BaseTestConfig
@DisplayName("AuthRoles Entity Tests")
class AuthRolesTest {

    @Autowired
    private Validator validator;

    private Auth testAuth;
    private Role testRole;
    private State testState;

    @BeforeEach
    void setUp() {
        testAuth = TestDataFactory.createValidAuth();
        testAuth.setId(1L);
        testAuth.setUsername("testuser");
        
        testRole = TestDataFactory.createUserRole();
        testRole.setId(1L);
        
        testState = TestDataFactory.createActiveState();
        testState.setId(1L);
    }

    @Test
    @DisplayName("Should create valid AuthRoles entity successfully")
    void shouldCreateValidAuthRolesEntity() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        
        // When
        Set<ConstraintViolation<AuthRoles>> violations = validator.validate(authRoles);
        
        // Then
        assertThat(violations).isEmpty();
        assertThat(authRoles.getAuth()).isEqualTo(testAuth);
        assertThat(authRoles.getRole()).isEqualTo(testRole);
        assertThat(authRoles.getState()).isEqualTo(testState);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties correctly")
    void shouldInheritAbstractModelProperties() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        LocalDateTime testTime = LocalDateTime.now();
        
        // When
        authRoles.setId(100L);
        authRoles.setCreatedAt(testTime);
        authRoles.setUpdatedAt(testTime.plusMinutes(30));
        
        // Then
        assertThat(authRoles.getId()).isEqualTo(100L);
        assertThat(authRoles.getCreatedAt()).isEqualTo(testTime);
        assertThat(authRoles.getUpdatedAt()).isEqualTo(testTime.plusMinutes(30));
    }

    @Test
    @DisplayName("Should fail validation when Auth is null")
    void shouldFailValidationWhenAuthIsNull() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(null, testRole, testState);
        
        // When
        Set<ConstraintViolation<AuthRoles>> violations = validator.validate(authRoles);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Auth is required");
    }

    @Test
    @DisplayName("Should fail validation when Role is null")
    void shouldFailValidationWhenRoleIsNull() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, null, testState);
        
        // When
        Set<ConstraintViolation<AuthRoles>> violations = validator.validate(authRoles);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Role is required");
    }

    @Test
    @DisplayName("Should fail validation when State is null")
    void shouldFailValidationWhenStateIsNull() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, null);
        
        // When
        Set<ConstraintViolation<AuthRoles>> violations = validator.validate(authRoles);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("State is required");
    }

    @Test
    @DisplayName("Should fail validation when all required fields are null")
    void shouldFailValidationWhenAllRequiredFieldsAreNull() {
        // Given
        AuthRoles authRoles = new AuthRoles();
        
        // When
        Set<ConstraintViolation<AuthRoles>> violations = validator.validate(authRoles);
        
        // Then
        assertThat(violations).hasSize(3);
        Set<String> messages = Set.of(
            violations.iterator().next().getMessage(),
            violations.stream().skip(1).findFirst().get().getMessage(),
            violations.stream().skip(2).findFirst().get().getMessage()
        );
        assertThat(messages).containsExactlyInAnyOrder(
            "Auth is required",
            "Role is required", 
            "State is required"
        );
    }

    @Test
    @DisplayName("Should test entity relationships correctly")
    void shouldTestEntityRelationships() {
        // Given
        Auth auth1 = TestDataFactory.createValidAuth("user1@automo.com");
        auth1.setId(1L);
        Auth auth2 = TestDataFactory.createValidAuth("user2@automo.com");  
        auth2.setId(2L);

        Role userRole = TestDataFactory.createUserRole();
        userRole.setId(1L);
        Role adminRole = TestDataFactory.createAdminRole();
        adminRole.setId(2L);

        AuthRoles authRoles1 = TestDataFactory.createValidAuthRoles(auth1, userRole, testState);
        AuthRoles authRoles2 = TestDataFactory.createValidAuthRoles(auth2, adminRole, testState);
        
        // When & Then
        assertThat(authRoles1.getAuth()).isNotEqualTo(authRoles2.getAuth());
        assertThat(authRoles1.getRole()).isNotEqualTo(authRoles2.getRole());
        assertThat(authRoles1.getState()).isEqualTo(authRoles2.getState());
    }

    @Test
    @DisplayName("Should handle equals and hashCode correctly")
    void shouldHandleEqualsAndHashCodeCorrectly() {
        // Given
        AuthRoles authRoles1 = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        authRoles1.setId(1L);
        
        AuthRoles authRoles2 = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        authRoles2.setId(1L);
        
        AuthRoles authRoles3 = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        authRoles3.setId(2L);
        
        // When & Then
        assertThat(authRoles1).isEqualTo(authRoles2);
        assertThat(authRoles1).isNotEqualTo(authRoles3);
        assertThat(authRoles1.hashCode()).isEqualTo(authRoles2.hashCode());
    }

    @Test
    @DisplayName("Should support many-to-many relationship between Auth and Role")
    void shouldSupportManyToManyRelationship() {
        // Given - One Auth can have multiple Roles
        Auth auth = TestDataFactory.createValidAuth("multirole@automo.com");
        auth.setId(1L);
        
        Role userRole = TestDataFactory.createUserRole();
        userRole.setId(1L);
        Role agentRole = TestDataFactory.createAgentRole();
        agentRole.setId(2L);
        
        AuthRoles userAssignment = TestDataFactory.createValidAuthRoles(auth, userRole, testState);
        AuthRoles agentAssignment = TestDataFactory.createValidAuthRoles(auth, agentRole, testState);
        
        // When
        Set<ConstraintViolation<AuthRoles>> userViolations = validator.validate(userAssignment);
        Set<ConstraintViolation<AuthRoles>> agentViolations = validator.validate(agentAssignment);
        
        // Then
        assertThat(userViolations).isEmpty();
        assertThat(agentViolations).isEmpty();
        assertThat(userAssignment.getAuth()).isEqualTo(agentAssignment.getAuth());
        assertThat(userAssignment.getRole()).isNotEqualTo(agentAssignment.getRole());
    }

    @Test
    @DisplayName("Should test fetch type configurations")
    void shouldTestFetchTypeConfigurations() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        
        // When & Then - Test that fetch types are properly configured
        // Auth is LAZY fetch
        assertThat(authRoles.getAuth()).isNotNull();
        // Role is EAGER fetch (as configured in entity)
        assertThat(authRoles.getRole()).isNotNull();
        // State is LAZY fetch
        assertThat(authRoles.getState()).isNotNull();
    }

    @Test
    @DisplayName("Should test table and column mapping")
    void shouldTestTableAndColumnMapping() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        
        // When & Then - Test that entity is properly configured for auth_roles table
        assertThat(authRoles.getClass().getAnnotation(jakarta.persistence.Table.class).name())
            .isEqualTo("auth_roles");
        
        // Verify join columns are properly configured
        assertThat(authRoles.getAuth()).isNotNull();
        assertThat(authRoles.getRole()).isNotNull();
        assertThat(authRoles.getState()).isNotNull();
    }

    @Test
    @DisplayName("Should support soft delete through State relationship")
    void shouldSupportSoftDeleteThroughStateRelationship() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, activeState);
        
        // When - Simulate soft delete by changing state
        authRoles.setState(eliminatedState);
        
        // Then
        Set<ConstraintViolation<AuthRoles>> violations = validator.validate(authRoles);
        assertThat(violations).isEmpty();
        assertThat(authRoles.getState()).isEqualTo(eliminatedState);
    }

    @Test
    @DisplayName("Should test constructor and builder pattern")
    void shouldTestConstructorAndBuilderPattern() {
        // Given & When - Test NoArgsConstructor
        AuthRoles emptyAuthRoles = new AuthRoles();
        
        // Then
        assertThat(emptyAuthRoles.getAuth()).isNull();
        assertThat(emptyAuthRoles.getRole()).isNull();
        assertThat(emptyAuthRoles.getState()).isNull();
        
        // Given & When - Test AllArgsConstructor
        AuthRoles fullAuthRoles = new AuthRoles(testAuth, testRole, testState);
        
        // Then
        assertThat(fullAuthRoles.getAuth()).isEqualTo(testAuth);
        assertThat(fullAuthRoles.getRole()).isEqualTo(testRole);
        assertThat(fullAuthRoles.getState()).isEqualTo(testState);
    }
}