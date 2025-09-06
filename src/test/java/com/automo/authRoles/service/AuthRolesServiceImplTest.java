package com.automo.authRoles.service;

import com.automo.auth.entity.Auth;
import com.automo.auth.service.AuthService;
import com.automo.authRoles.dto.AuthRolesDto;
import com.automo.authRoles.entity.AuthRoles;
import com.automo.authRoles.repository.AuthRolesRepository;
import com.automo.authRoles.response.AuthRolesResponse;
import com.automo.role.entity.Role;
import com.automo.role.service.RoleService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Comprehensive service layer tests for AuthRoles
 * Testing business logic, service-to-service communication, and relationship management
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthRoles Service Implementation Tests")
class AuthRolesServiceImplTest {

    @Mock
    private AuthRolesRepository authRolesRepository;

    @Mock
    private AuthService authService;

    @Mock
    private RoleService roleService;

    @Mock
    private StateService stateService;

    @InjectMocks
    private AuthRolesServiceImpl authRolesService;

    private Auth testAuth;
    private Role testRole;
    private State testState;
    private State eliminatedState;
    private AuthRoles testAuthRoles;
    private AuthRolesDto testAuthRolesDto;

    @BeforeEach
    void setUp() {
        testAuth = TestDataFactory.createValidAuth("test@automo.com");
        testAuth.setId(1L);
        testAuth.setUsername("testuser");

        testRole = TestDataFactory.createUserRole();
        testRole.setId(1L);

        testState = TestDataFactory.createActiveState();
        testState.setId(1L);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);

        testAuthRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, testState);
        testAuthRoles.setId(1L);
        testAuthRoles.setCreatedAt(LocalDateTime.now());
        testAuthRoles.setUpdatedAt(LocalDateTime.now());

        testAuthRolesDto = TestDataFactory.createValidAuthRolesDto(1L, 1L, 1L);
    }

    @Test
    @DisplayName("Should create AuthRoles successfully")
    void shouldCreateAuthRolesSuccessfully() {
        // Given
        when(authRolesRepository.existsByAuthIdAndRoleId(1L, 1L)).thenReturn(false);
        when(authService.findById(1L)).thenReturn(testAuth);
        when(roleService.findById(1L)).thenReturn(testRole);
        when(stateService.findById(1L)).thenReturn(testState);
        when(authRolesRepository.save(any(AuthRoles.class))).thenReturn(testAuthRoles);

        // When
        AuthRolesResponse response = authRolesService.createAuthRoles(testAuthRolesDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.authId()).isEqualTo(1L);
        assertThat(response.roleId()).isEqualTo(1L);
        assertThat(response.stateId()).isEqualTo(1L);
        
        verify(authRolesRepository).existsByAuthIdAndRoleId(1L, 1L);
        verify(authService).findById(1L);
        verify(roleService).findById(1L);
        verify(stateService).findById(1L);
        verify(authRolesRepository).save(any(AuthRoles.class));
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate AuthRoles")
    void shouldThrowExceptionWhenCreatingDuplicateAuthRoles() {
        // Given
        when(authRolesRepository.existsByAuthIdAndRoleId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authRolesService.createAuthRoles(testAuthRolesDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Auth already has this role assigned");

        verify(authRolesRepository).existsByAuthIdAndRoleId(1L, 1L);
        verify(authService, never()).findById(any());
        verify(authRolesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get AuthRoles by ID successfully")
    void shouldGetAuthRolesByIdSuccessfully() {
        // Given
        when(authRolesRepository.findById(1L)).thenReturn(Optional.of(testAuthRoles));

        // When
        AuthRolesResponse response = authRolesService.getAuthRolesById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.authEmail()).isEqualTo(testAuth.getEmail());
        assertThat(response.roleRole()).isEqualTo(testRole.getRole());
        
        verify(authRolesRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when AuthRoles not found by ID")
    void shouldThrowExceptionWhenAuthRolesNotFoundById() {
        // Given
        when(authRolesRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authRolesService.getAuthRolesById(1L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("AuthRoles with ID 1 not found");

        verify(authRolesRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get all AuthRoles successfully excluding eliminated")
    void shouldGetAllAuthRolesSuccessfullyExcludingEliminated() {
        // Given
        AuthRoles eliminatedAuthRoles = TestDataFactory.createValidAuthRoles(testAuth, testRole, eliminatedState);
        eliminatedAuthRoles.setId(2L);
        
        List<AuthRoles> authRolesList = Arrays.asList(testAuthRoles, eliminatedAuthRoles);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(authRolesRepository.findAll()).thenReturn(authRolesList);

        // When
        List<AuthRolesResponse> responses = authRolesService.getAllAuthRoles();

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        
        verify(stateService).getEliminatedState();
        verify(authRolesRepository).findAll();
    }

    @Test
    @DisplayName("Should update AuthRoles successfully")
    void shouldUpdateAuthRolesSuccessfully() {
        // Given
        when(authRolesRepository.findById(1L)).thenReturn(Optional.of(testAuthRoles));
        when(authService.findById(1L)).thenReturn(testAuth);
        when(roleService.findById(1L)).thenReturn(testRole);
        when(stateService.findById(1L)).thenReturn(testState);
        when(authRolesRepository.save(any(AuthRoles.class))).thenReturn(testAuthRoles);

        // When
        AuthRolesResponse response = authRolesService.updateAuthRoles(1L, testAuthRolesDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        
        verify(authRolesRepository).findById(1L);
        verify(authService).findById(1L);
        verify(roleService).findById(1L);
        verify(stateService).findById(1L);
        verify(authRolesRepository).save(testAuthRoles);
    }

    @Test
    @DisplayName("Should throw exception when updating to duplicate Auth-Role combination")
    void shouldThrowExceptionWhenUpdatingToDuplicateAuthRoleCombination() {
        // Given
        AuthRolesDto updateDto = TestDataFactory.createValidAuthRolesDto(2L, 2L, 1L);
        when(authRolesRepository.findById(1L)).thenReturn(Optional.of(testAuthRoles));
        when(authRolesRepository.existsByAuthIdAndRoleId(2L, 2L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authRolesService.updateAuthRoles(1L, updateDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Auth already has this role assigned");

        verify(authRolesRepository).findById(1L);
        verify(authRolesRepository).existsByAuthIdAndRoleId(2L, 2L);
        verify(authRolesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should perform soft delete successfully")
    void shouldPerformSoftDeleteSuccessfully() {
        // Given
        when(authRolesRepository.findById(1L)).thenReturn(Optional.of(testAuthRoles));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(authRolesRepository.save(any(AuthRoles.class))).thenReturn(testAuthRoles);

        // When
        authRolesService.deleteAuthRoles(1L);

        // Then
        verify(authRolesRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(authRolesRepository).save(testAuthRoles);
        // Verify state was changed to eliminated
        assertThat(testAuthRoles.getState()).isEqualTo(eliminatedState);
    }

    @Test
    @DisplayName("Should get AuthRoles by Auth ID")
    void shouldGetAuthRolesByAuthId() {
        // Given
        List<AuthRoles> authRolesList = Arrays.asList(testAuthRoles);
        when(authRolesRepository.findByAuthId(1L)).thenReturn(authRolesList);

        // When
        List<AuthRolesResponse> responses = authRolesService.getAuthRolesByAuthId(1L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).authId()).isEqualTo(1L);
        
        verify(authRolesRepository).findByAuthId(1L);
    }

    @Test
    @DisplayName("Should get AuthRoles by Role ID")
    void shouldGetAuthRolesByRoleId() {
        // Given
        List<AuthRoles> authRolesList = Arrays.asList(testAuthRoles);
        when(authRolesRepository.findByRoleId(1L)).thenReturn(authRolesList);

        // When
        List<AuthRolesResponse> responses = authRolesService.getAuthRolesByRoleId(1L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).roleId()).isEqualTo(1L);
        
        verify(authRolesRepository).findByRoleId(1L);
    }

    @Test
    @DisplayName("Should get AuthRoles by State ID")
    void shouldGetAuthRolesByStateId() {
        // Given
        List<AuthRoles> authRolesList = Arrays.asList(testAuthRoles);
        when(authRolesRepository.findByStateId(1L)).thenReturn(authRolesList);

        // When
        List<AuthRolesResponse> responses = authRolesService.getAuthRolesByStateId(1L);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).stateId()).isEqualTo(1L);
        
        verify(authRolesRepository).findByStateId(1L);
    }

    @Test
    @DisplayName("Should find by ID for service-to-service communication")
    void shouldFindByIdForServiceToServiceCommunication() {
        // Given
        when(authRolesRepository.findById(1L)).thenReturn(Optional.of(testAuthRoles));

        // When
        AuthRoles found = authRolesService.findById(1L);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        
        verify(authRolesRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find by ID and State ID for service-to-service communication")
    void shouldFindByIdAndStateIdForServiceToServiceCommunication() {
        // Given
        when(authRolesRepository.findById(1L)).thenReturn(Optional.of(testAuthRoles));

        // When
        AuthRoles found = authRolesService.findByIdAndStateId(1L, 1L);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getState().getId()).isEqualTo(1L);
        
        verify(authRolesRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when finding by ID and State ID with wrong state")
    void shouldThrowExceptionWhenFindingByIdAndStateIdWithWrongState() {
        // Given
        when(authRolesRepository.findById(1L)).thenReturn(Optional.of(testAuthRoles));

        // When & Then
        assertThatThrownBy(() -> authRolesService.findByIdAndStateId(1L, 2L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("AuthRoles with ID 1 and state ID 2 not found");

        verify(authRolesRepository).findById(1L);
    }

    @Test
    @DisplayName("Should create AuthRoles with entities for inter-service communication")
    void shouldCreateAuthRolesWithEntitiesForInterServiceCommunication() {
        // Given
        when(authRolesRepository.existsByAuthIdAndRoleId(1L, 1L)).thenReturn(false);
        when(authRolesRepository.save(any(AuthRoles.class))).thenReturn(testAuthRoles);

        // When
        authRolesService.createAuthRolesWithEntities(testAuth, testRole, testState);

        // Then
        verify(authRolesRepository).existsByAuthIdAndRoleId(1L, 1L);
        verify(authRolesRepository).save(any(AuthRoles.class));
    }

    @Test
    @DisplayName("Should throw exception when creating AuthRoles with entities for existing association")
    void shouldThrowExceptionWhenCreatingAuthRolesWithEntitiesForExistingAssociation() {
        // Given
        when(authRolesRepository.existsByAuthIdAndRoleId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authRolesService.createAuthRolesWithEntities(testAuth, testRole, testState))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Auth already has this role assigned");

        verify(authRolesRepository).existsByAuthIdAndRoleId(1L, 1L);
        verify(authRolesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find by Auth ID for service-to-service communication")
    void shouldFindByAuthIdForServiceToServiceCommunication() {
        // Given
        List<AuthRoles> authRolesList = Arrays.asList(testAuthRoles);
        when(authRolesRepository.findByAuthId(1L)).thenReturn(authRolesList);

        // When
        List<AuthRoles> found = authRolesService.findByAuthId(1L);

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getId()).isEqualTo(1L);
        
        verify(authRolesRepository).findByAuthId(1L);
    }

    @Test
    @DisplayName("Should save entity for service-to-service communication")
    void shouldSaveEntityForServiceToServiceCommunication() {
        // Given
        when(authRolesRepository.save(testAuthRoles)).thenReturn(testAuthRoles);

        // When
        AuthRoles saved = authRolesService.save(testAuthRoles);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        
        verify(authRolesRepository).save(testAuthRoles);
    }

    @Test
    @DisplayName("Should delete entity for service-to-service communication")
    void shouldDeleteEntityForServiceToServiceCommunication() {
        // When
        authRolesService.delete(testAuthRoles);

        // Then
        verify(authRolesRepository).delete(testAuthRoles);
    }

    @Test
    @DisplayName("Should handle multiple roles for same auth correctly")
    void shouldHandleMultipleRolesForSameAuthCorrectly() {
        // Given
        Role adminRole = TestDataFactory.createAdminRole();
        adminRole.setId(2L);
        
        AuthRoles adminAuthRoles = TestDataFactory.createValidAuthRoles(testAuth, adminRole, testState);
        adminAuthRoles.setId(2L);
        
        List<AuthRoles> authRolesList = Arrays.asList(testAuthRoles, adminAuthRoles);
        when(authRolesRepository.findByAuthId(1L)).thenReturn(authRolesList);

        // When
        List<AuthRolesResponse> responses = authRolesService.getAuthRolesByAuthId(1L);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses).allSatisfy(response -> 
            assertThat(response.authId()).isEqualTo(1L)
        );
        
        verify(authRolesRepository).findByAuthId(1L);
    }

    @Test
    @DisplayName("Should use default state ID when null in findByIdAndStateId")
    void shouldUseDefaultStateIdWhenNullInFindByIdAndStateId() {
        // Given
        when(authRolesRepository.findById(1L)).thenReturn(Optional.of(testAuthRoles));

        // When
        AuthRoles found = authRolesService.findByIdAndStateId(1L, null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        
        verify(authRolesRepository).findById(1L);
    }

    @Test
    @DisplayName("Should test response mapping correctly")
    void shouldTestResponseMappingCorrectly() {
        // Given
        when(authRolesRepository.findById(1L)).thenReturn(Optional.of(testAuthRoles));

        // When
        AuthRolesResponse response = authRolesService.getAuthRolesById(1L);

        // Then
        assertThat(response.id()).isEqualTo(testAuthRoles.getId());
        assertThat(response.authId()).isEqualTo(testAuth.getId());
        assertThat(response.authEmail()).isEqualTo(testAuth.getEmail());
        assertThat(response.authUsername()).isEqualTo(testAuth.getUsername());
        assertThat(response.roleId()).isEqualTo(testRole.getId());
        assertThat(response.roleRole()).isEqualTo(testRole.getRole());
        assertThat(response.stateId()).isEqualTo(testState.getId());
        assertThat(response.stateState()).isEqualTo(testState.getState());
        assertThat(response.createdAt()).isEqualTo(testAuthRoles.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(testAuthRoles.getUpdatedAt());
    }
}