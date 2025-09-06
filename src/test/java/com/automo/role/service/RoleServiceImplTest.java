package com.automo.role.service;

import com.automo.role.dto.RoleDto;
import com.automo.role.entity.Role;
import com.automo.role.repository.RoleRepository;
import com.automo.role.response.RoleResponse;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for RoleServiceImpl")
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;
    private RoleDto testRoleDto;

    @BeforeEach
    void setUp() {
        testRole = TestDataFactory.createUserRole();
        testRole.setId(1L);
        testRole.setDescription("User role");

        testRoleDto = TestDataFactory.createValidRoleDto("USER", "User role");
    }

    @Test
    @DisplayName("Should create role successfully")
    void shouldCreateRoleSuccessfully() {
        // Given
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // When
        RoleResponse response = roleService.createRole(testRoleDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("USER", response.role());
        assertEquals("User role", response.description());

        verify(roleRepository).save(any(Role.class));
    }

    @Test
    @DisplayName("Should update role successfully")
    void shouldUpdateRoleSuccessfully() {
        // Given
        RoleDto updateDto = TestDataFactory.createValidRoleDto("ADMIN", "Administrator role");
        
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // When
        RoleResponse response = roleService.updateRole(1L, updateDto);

        // Then
        assertNotNull(response);
        assertEquals("ADMIN", testRole.getRole());
        assertEquals("Administrator role", testRole.getDescription());

        verify(roleRepository).findById(1L);
        verify(roleRepository).save(testRole);
    }

    @Test
    @DisplayName("Should get all roles")
    void shouldGetAllRoles() {
        // Given
        Role adminRole = TestDataFactory.createAdminRole();
        adminRole.setId(2L);
        
        List<Role> roles = Arrays.asList(testRole, adminRole);
        when(roleRepository.findAll()).thenReturn(roles);

        // When
        List<RoleResponse> responses = roleService.getAllRoles();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(roleRepository).findAll();
    }

    @Test
    @DisplayName("Should delete role successfully")
    void shouldDeleteRoleSuccessfully() {
        // Given
        when(roleRepository.existsById(1L)).thenReturn(true);

        // When
        roleService.deleteRole(1L);

        // Then
        verify(roleRepository).existsById(1L);
        verify(roleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent role")
    void shouldThrowExceptionWhenDeletingNonExistentRole() {
        // Given
        when(roleRepository.existsById(999L)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                roleService.deleteRole(999L));

        assertEquals("Role with ID 999 not found", exception.getMessage());
        verify(roleRepository).existsById(999L);
        verify(roleRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should find by role name")
    void shouldFindByRoleName() {
        // Given
        when(roleRepository.findByRole("USER")).thenReturn(Optional.of(testRole));

        // When
        Role result = roleService.findByRole("USER");

        // Then
        assertNotNull(result);
        assertEquals("USER", result.getRole());

        verify(roleRepository).findByRole("USER");
    }
}