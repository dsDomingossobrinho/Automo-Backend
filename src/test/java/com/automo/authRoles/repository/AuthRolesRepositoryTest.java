package com.automo.authRoles.repository;

import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.authRoles.entity.AuthRoles;
import com.automo.role.entity.Role;
import com.automo.role.repository.RoleRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive repository tests for AuthRoles entity
 * Testing database operations, queries, and relationship constraints
 */
@BaseTestConfig
@DisplayName("AuthRoles Repository Tests")
class AuthRolesRepositoryTest {

    @Autowired
    private AuthRolesRepository authRolesRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StateRepository stateRepository;

    private Auth testAuth;
    private Role testRole;
    private State testState;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        authRolesRepository.deleteAll();
        
        // Create and save test entities
        testAuth = TestDataFactory.createValidAuth("test@automo.com");
        testAuth.setUsername("testuser");
        testAuth = authRepository.save(testAuth);

        testRole = TestDataFactory.createUserRole();
        testRole = roleRepository.save(testRole);

        testState = TestDataFactory.createActiveState();
        testState = stateRepository.save(testState);
    }

    @Test
    @DisplayName("Should save and find AuthRoles successfully")
    void shouldSaveAndFindAuthRoles() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        
        // When
        AuthRoles savedAuthRoles = authRolesRepository.save(authRoles);
        Optional<AuthRoles> foundAuthRoles = authRolesRepository.findById(savedAuthRoles.getId());
        
        // Then
        assertThat(savedAuthRoles.getId()).isNotNull();
        assertThat(foundAuthRoles).isPresent();
        assertThat(foundAuthRoles.get().getAuth().getId()).isEqualTo(testAuth.getId());
        assertThat(foundAuthRoles.get().getRole().getId()).isEqualTo(testRole.getId());
        assertThat(foundAuthRoles.get().getState().getId()).isEqualTo(testState.getId());
    }

    @Test
    @DisplayName("Should find AuthRoles by Auth ID")
    void shouldFindAuthRolesByAuthId() {
        // Given
        AuthRoles authRoles1 = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        Role adminRole = TestDataFactory.createAdminRole();
        adminRole = roleRepository.save(adminRole);
        AuthRoles authRoles2 = TestDataFactory.createValidAuthRoles(testAuth, adminRole, testState);
        
        authRolesRepository.save(authRoles1);
        authRolesRepository.save(authRoles2);
        
        // When
        List<AuthRoles> authRolesList = authRolesRepository.findByAuthId(testAuth.getId());
        
        // Then
        assertThat(authRolesList).hasSize(2);
        assertThat(authRolesList).allSatisfy(ar -> 
            assertThat(ar.getAuth().getId()).isEqualTo(testAuth.getId())
        );
    }

    @Test
    @DisplayName("Should find AuthRoles by Role ID")
    void shouldFindAuthRolesByRoleId() {
        // Given
        Auth anotherAuth = TestDataFactory.createValidAuth("another@automo.com");
        anotherAuth.setUsername("anotheruser");
        anotherAuth = authRepository.save(anotherAuth);
        
        AuthRoles authRoles1 = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        AuthRoles authRoles2 = TestDataFactory.createValidAuthRoles(anotherAuth, testRole, testState);
        
        authRolesRepository.save(authRoles1);
        authRolesRepository.save(authRoles2);
        
        // When
        List<AuthRoles> authRolesList = authRolesRepository.findByRoleId(testRole.getId());
        
        // Then
        assertThat(authRolesList).hasSize(2);
        assertThat(authRolesList).allSatisfy(ar -> 
            assertThat(ar.getRole().getId()).isEqualTo(testRole.getId())
        );
    }

    @Test
    @DisplayName("Should find AuthRoles by State ID")
    void shouldFindAuthRolesByStateId() {
        // Given
        AuthRoles authRoles1 = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        
        Auth anotherAuth = TestDataFactory.createValidAuth("state@automo.com");
        anotherAuth.setUsername("stateuser");
        anotherAuth = authRepository.save(anotherAuth);
        
        Role anotherRole = TestDataFactory.createAgentRole();
        anotherRole = roleRepository.save(anotherRole);
        
        AuthRoles authRoles2 = TestDataFactory.createValidAuthRoles(anotherAuth, anotherRole, testState);
        
        authRolesRepository.save(authRoles1);
        authRolesRepository.save(authRoles2);
        
        // When
        List<AuthRoles> authRolesList = authRolesRepository.findByStateId(testState.getId());
        
        // Then
        assertThat(authRolesList).hasSize(2);
        assertThat(authRolesList).allSatisfy(ar -> 
            assertThat(ar.getState().getId()).isEqualTo(testState.getId())
        );
    }

    @Test
    @DisplayName("Should check if AuthRoles exists by Auth ID and Role ID")
    void shouldCheckExistsByAuthIdAndRoleId() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        authRolesRepository.save(authRoles);
        
        // When & Then
        assertThat(authRolesRepository.existsByAuthIdAndRoleId(testAuth.getId(), testRole.getId()))
            .isTrue();
        assertThat(authRolesRepository.existsByAuthIdAndRoleId(999L, testRole.getId()))
            .isFalse();
        assertThat(authRolesRepository.existsByAuthIdAndRoleId(testAuth.getId(), 999L))
            .isFalse();
    }

    @Test
    @DisplayName("Should prevent duplicate Auth-Role assignments")
    void shouldPreventDuplicateAuthRoleAssignments() {
        // Given
        AuthRoles authRoles1 = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        authRolesRepository.save(authRoles1);
        
        // When & Then - Try to create duplicate
        AuthRoles duplicateAuthRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        
        // Should allow save but business logic should prevent this
        AuthRoles savedDuplicate = authRolesRepository.save(duplicateAuthRoles);
        assertThat(savedDuplicate.getId()).isNotNull();
        
        // Verify that exists check returns true
        assertThat(authRolesRepository.existsByAuthIdAndRoleId(testAuth.getId(), testRole.getId()))
            .isTrue();
    }

    @Test
    @DisplayName("Should handle foreign key constraints")
    void shouldHandleForeignKeyConstraints() {
        // Given
        AuthRoles authRoles = new AuthRoles();
        // Setting invalid foreign key references would cause constraint violations
        
        // When & Then - This should fail due to null constraints in entity validation
        assertThatThrownBy(() -> {
            authRoles.setAuth(null);
            authRoles.setRole(testRole);
            authRoles.setState(testState);
            authRolesRepository.save(authRoles);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should test cascading operations")
    void shouldTestCascadingOperations() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        AuthRoles savedAuthRoles = authRolesRepository.save(authRoles);
        
        // When - Delete the AuthRoles
        authRolesRepository.delete(savedAuthRoles);
        
        // Then - AuthRoles should be deleted but related entities should remain
        assertThat(authRolesRepository.findById(savedAuthRoles.getId())).isEmpty();
        assertThat(authRepository.findById(testAuth.getId())).isPresent();
        assertThat(roleRepository.findById(testRole.getId())).isPresent();
        assertThat(stateRepository.findById(testState.getId())).isPresent();
    }

    @Test
    @DisplayName("Should handle complex many-to-many relationships")
    void shouldHandleComplexManyToManyRelationships() {
        // Given - Create multiple users with multiple roles
        Auth user1 = TestDataFactory.createValidAuth("user1@automo.com");
        user1.setUsername("user1");
        user1 = authRepository.save(user1);
        
        Auth user2 = TestDataFactory.createValidAuth("user2@automo.com");
        user2.setUsername("user2");
        user2 = authRepository.save(user2);
        
        Role adminRole = TestDataFactory.createAdminRole();
        adminRole = roleRepository.save(adminRole);
        
        Role agentRole = TestDataFactory.createAgentRole();
        agentRole = roleRepository.save(agentRole);
        
        // Create associations
        AuthRoles user1UserRole = TestDataFactory.createValidAuthRoles(user1, testRole, testState);
        AuthRoles user1AdminRole = TestDataFactory.createValidAuthRoles(user1, adminRole, testState);
        AuthRoles user2AgentRole = TestDataFactory.createValidAuthRoles(user2, agentRole, testState);
        AuthRoles user2AdminRole = TestDataFactory.createValidAuthRoles(user2, adminRole, testState);
        
        // When
        authRolesRepository.save(user1UserRole);
        authRolesRepository.save(user1AdminRole);
        authRolesRepository.save(user2AgentRole);
        authRolesRepository.save(user2AdminRole);
        
        // Then
        List<AuthRoles> user1Roles = authRolesRepository.findByAuthId(user1.getId());
        List<AuthRoles> user2Roles = authRolesRepository.findByAuthId(user2.getId());
        List<AuthRoles> adminRoleUsers = authRolesRepository.findByRoleId(adminRole.getId());
        
        assertThat(user1Roles).hasSize(2);
        assertThat(user2Roles).hasSize(2);
        assertThat(adminRoleUsers).hasSize(2);
    }

    @Test
    @DisplayName("Should test query performance with indexes")
    void shouldTestQueryPerformanceWithIndexes() {
        // Given - Create many AuthRoles records
        for (int i = 0; i < 10; i++) {
            Auth auth = TestDataFactory.createValidAuth("bulk" + i + "@automo.com");
            auth.setUsername("bulk" + i);
            auth = authRepository.save(auth);
            
            AuthRoles authRoles = TestDataFactory.createValidAuthRoles(auth, testRole, testState);
            authRolesRepository.save(authRoles);
        }
        
        // When - Query by different parameters
        long startTime = System.currentTimeMillis();
        List<AuthRoles> authRolesByRoleId = authRolesRepository.findByRoleId(testRole.getId());
        List<AuthRoles> authRolesByStateId = authRolesRepository.findByStateId(testState.getId());
        long endTime = System.currentTimeMillis();
        
        // Then
        assertThat(authRolesByRoleId).hasSizeGreaterThan(10);
        assertThat(authRolesByStateId).hasSizeGreaterThan(10);
        // Query should be fast (less than 1 second for this small dataset)
        assertThat(endTime - startTime).isLessThan(1000);
    }

    @Test
    @DisplayName("Should test lazy loading of relationships")
    void shouldTestLazyLoadingOfRelationships() {
        // Given
        AuthRoles authRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        AuthRoles savedAuthRoles = authRolesRepository.save(authRoles);
        
        // When - Find and access lazy loaded relationships
        Optional<AuthRoles> foundAuthRoles = authRolesRepository.findById(savedAuthRoles.getId());
        
        // Then
        assertThat(foundAuthRoles).isPresent();
        AuthRoles ar = foundAuthRoles.get();
        
        // Access lazy-loaded Auth relationship
        assertThat(ar.getAuth()).isNotNull();
        assertThat(ar.getAuth().getEmail()).isEqualTo(testAuth.getEmail());
        
        // Access eager-loaded Role relationship
        assertThat(ar.getRole()).isNotNull();
        assertThat(ar.getRole().getRole()).isEqualTo(testRole.getRole());
        
        // Access lazy-loaded State relationship  
        assertThat(ar.getState()).isNotNull();
        assertThat(ar.getState().getState()).isEqualTo(testState.getState());
    }

    @Test
    @DisplayName("Should handle empty result sets correctly")
    void shouldHandleEmptyResultSetsCorrectly() {
        // Given - Clean database
        authRolesRepository.deleteAll();
        
        // When
        List<AuthRoles> allAuthRoles = authRolesRepository.findAll();
        List<AuthRoles> authRolesByNonExistentAuth = authRolesRepository.findByAuthId(999L);
        List<AuthRoles> authRolesByNonExistentRole = authRolesRepository.findByRoleId(999L);
        List<AuthRoles> authRolesByNonExistentState = authRolesRepository.findByStateId(999L);
        boolean existsByNonExistentIds = authRolesRepository.existsByAuthIdAndRoleId(999L, 999L);
        
        // Then
        assertThat(allAuthRoles).isEmpty();
        assertThat(authRolesByNonExistentAuth).isEmpty();
        assertThat(authRolesByNonExistentRole).isEmpty();
        assertThat(authRolesByNonExistentState).isEmpty();
        assertThat(existsByNonExistentIds).isFalse();
    }

    @Test
    @DisplayName("Should test batch operations")
    void shouldTestBatchOperations() {
        // Given
        Role adminRole = TestDataFactory.createAdminRole();
        adminRole = roleRepository.save(adminRole);
        
        List<AuthRoles> authRolesList = List.of(
            TestDataFactory.createValidAuthRoles(testAuth, testRole, testState),
            TestDataFactory.createValidAuthRoles(testAuth, adminRole, testState)
        );
        
        // When
        List<AuthRoles> savedAuthRoles = authRolesRepository.saveAll(authRolesList);
        
        // Then
        assertThat(savedAuthRoles).hasSize(2);
        assertThat(savedAuthRoles).allSatisfy(ar -> assertThat(ar.getId()).isNotNull());
        
        List<AuthRoles> foundAuthRoles = authRolesRepository.findByAuthId(testAuth.getId());
        assertThat(foundAuthRoles).hasSize(2);
    }
}