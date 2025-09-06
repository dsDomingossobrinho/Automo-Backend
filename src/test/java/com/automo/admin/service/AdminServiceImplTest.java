package com.automo.admin.service;

import com.automo.admin.dto.AdminDto;
import com.automo.admin.entity.Admin;
import com.automo.admin.repository.AdminRepository;
import com.automo.admin.response.AdminResponse;
import com.automo.auth.entity.Auth;
import com.automo.auth.service.AuthEntityCreationService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for AdminServiceImpl")
class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private StateService stateService;

    @Mock
    private AuthEntityCreationService authEntityCreationService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin testAdmin;
    private Auth testAuth;
    private State activeState;
    private State eliminatedState;
    private AdminDto testAdminDto;

    @BeforeEach
    void setUp() {
        testAuth = TestDataFactory.createValidAuth("admin@automo.com");
        testAuth.setId(1L);
        testAuth.setUsername("admin_user");
        
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        testAdmin = TestDataFactory.createValidAdmin(testAuth, activeState);
        testAdmin.setId(1L);
        testAdmin.setEmail("admin@automo.com");
        
        testAdminDto = new AdminDto(
            "admin@automo.com",
            "Admin User",
            "profile.jpg",
            "password123",
            "912345678",
            1L,
            1L
        );
    }

    @Test
    @DisplayName("Should create admin successfully")
    void shouldCreateAdminSuccessfully() {
        // Given
        when(stateService.findById(1L)).thenReturn(activeState);
        when(adminRepository.save(any(Admin.class))).thenReturn(testAdmin);
        when(authEntityCreationService.createAuthForEntity(
            anyString(), anyString(), anyString(), anyString(), any(Long.class), any(State.class), anyString(), anyString()))
            .thenReturn(testAuth);

        // When
        AdminResponse result = adminService.createAdmin(testAdminDto);

        // Then
        assertNotNull(result);
        assertEquals("admin@automo.com", result.email());
        assertEquals("Admin User", result.name());
        assertEquals("profile.jpg", result.img());
        assertEquals(1L, result.authId());
        assertEquals("admin_user", result.username());
        assertEquals(1L, result.stateId());
        assertEquals("ACTIVE", result.state());
        
        verify(stateService).findById(1L);
        verify(adminRepository, times(2)).save(any(Admin.class)); // Once for initial save, once for auth update
        verify(authEntityCreationService).createAuthForEntity(
            "admin@automo.com", "Admin User", "password123", "912345678", 1L, activeState, "ADMIN", "ADMIN");
    }

    @Test
    @DisplayName("Should throw exception when creating admin with invalid state")
    void shouldThrowExceptionWhenCreatingAdminWithInvalidState() {
        // Given
        when(stateService.findById(999L)).thenThrow(new EntityNotFoundException("State not found"));
        
        AdminDto invalidDto = new AdminDto(
            "admin@automo.com", "Admin User", "profile.jpg", "password123", "912345678", 1L, 999L);

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> adminService.createAdmin(invalidDto));
        
        verify(stateService).findById(999L);
        verify(adminRepository, never()).save(any(Admin.class));
        verify(authEntityCreationService, never()).createAuthForEntity(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should update admin successfully")
    void shouldUpdateAdminSuccessfully() {
        // Given
        Long adminId = 1L;
        when(adminRepository.findByIdWithAuthAndState(adminId)).thenReturn(Optional.of(testAdmin));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(adminRepository.save(any(Admin.class))).thenReturn(testAdmin);

        // When
        AdminResponse result = adminService.updateAdmin(adminId, testAdminDto);

        // Then
        assertNotNull(result);
        assertEquals("admin@automo.com", result.email());
        assertEquals("Admin User", result.name());
        
        verify(adminRepository).findByIdWithAuthAndState(adminId);
        verify(stateService).findById(1L);
        verify(adminRepository).save(any(Admin.class));
        verify(authEntityCreationService).updateAuthForEntity(
            any(Auth.class), eq("admin@automo.com"), eq("password123"), eq("912345678"), eq(1L), eq(activeState));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing admin")
    void shouldThrowExceptionWhenUpdatingNonExistingAdmin() {
        // Given
        Long adminId = 999L;
        when(adminRepository.findByIdWithAuthAndState(adminId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> adminService.updateAdmin(adminId, testAdminDto));
        
        verify(adminRepository).findByIdWithAuthAndState(adminId);
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should get all admins excluding eliminated")
    void shouldGetAllAdminsExcludingEliminated() {
        // Given
        Admin admin1 = TestDataFactory.createValidAdmin(testAuth, activeState);
        admin1.setId(1L);
        admin1.setEmail("admin1@automo.com");
        
        Admin admin2 = TestDataFactory.createValidAdmin(testAuth, activeState);
        admin2.setId(2L);
        admin2.setEmail("admin2@automo.com");
        
        Admin eliminatedAdmin = TestDataFactory.createValidAdmin(testAuth, eliminatedState);
        eliminatedAdmin.setId(3L);
        eliminatedAdmin.setEmail("eliminated@automo.com");
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(adminRepository.findAllWithAuthAndState()).thenReturn(Arrays.asList(admin1, admin2, eliminatedAdmin));

        // When
        List<AdminResponse> result = adminService.getAllAdmins();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Only active admins, eliminated should be filtered out
        assertTrue(result.stream().anyMatch(r -> r.email().equals("admin1@automo.com")));
        assertTrue(result.stream().anyMatch(r -> r.email().equals("admin2@automo.com")));
        assertFalse(result.stream().anyMatch(r -> r.email().equals("eliminated@automo.com")));
        
        verify(stateService).getEliminatedState();
        verify(adminRepository).findAllWithAuthAndState();
    }

    @Test
    @DisplayName("Should get admin by id successfully")
    void shouldGetAdminByIdSuccessfully() {
        // Given
        Long adminId = 1L;
        when(adminRepository.findByIdWithAuthAndState(adminId)).thenReturn(Optional.of(testAdmin));

        // When
        Admin result = adminService.getAdminById(adminId);

        // Then
        assertNotNull(result);
        assertEquals(testAdmin.getId(), result.getId());
        assertEquals(testAdmin.getName(), result.getName());
        assertEquals(testAdmin.getEmail(), result.getEmail());
        
        verify(adminRepository).findByIdWithAuthAndState(adminId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existing admin")
    void shouldThrowExceptionWhenGettingNonExistingAdmin() {
        // Given
        Long adminId = 999L;
        when(adminRepository.findByIdWithAuthAndState(adminId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> adminService.getAdminById(adminId));
        
        verify(adminRepository).findByIdWithAuthAndState(adminId);
    }

    @Test
    @DisplayName("Should get admin by id response successfully")
    void shouldGetAdminByIdResponseSuccessfully() {
        // Given
        Long adminId = 1L;
        when(adminRepository.findByIdWithAuthAndState(adminId)).thenReturn(Optional.of(testAdmin));

        // When
        AdminResponse result = adminService.getAdminByIdResponse(adminId);

        // Then
        assertNotNull(result);
        assertEquals(testAdmin.getEmail(), result.email());
        assertEquals(testAdmin.getName(), result.name());
        
        verify(adminRepository).findByIdWithAuthAndState(adminId);
    }

    @Test
    @DisplayName("Should get admin by email successfully")
    void shouldGetAdminByEmailSuccessfully() {
        // Given
        String email = "admin@automo.com";
        when(adminRepository.findByEmailWithAuthAndState(email)).thenReturn(Optional.of(testAdmin));

        // When
        AdminResponse result = adminService.getAdminByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.email());
        assertEquals(testAdmin.getName(), result.name());
        
        verify(adminRepository).findByEmailWithAuthAndState(email);
    }

    @Test
    @DisplayName("Should throw exception when admin email not found")
    void shouldThrowExceptionWhenAdminEmailNotFound() {
        // Given
        String email = "nonexistent@automo.com";
        when(adminRepository.findByEmailWithAuthAndState(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> adminService.getAdminByEmail(email));
        
        verify(adminRepository).findByEmailWithAuthAndState(email);
    }

    @Test
    @DisplayName("Should get admin by auth id successfully")
    void shouldGetAdminByAuthIdSuccessfully() {
        // Given
        Long authId = 1L;
        when(adminRepository.findByAuthIdWithAuthAndState(authId)).thenReturn(Optional.of(testAdmin));

        // When
        AdminResponse result = adminService.getAdminByAuthId(authId);

        // Then
        assertNotNull(result);
        assertEquals(testAdmin.getEmail(), result.email());
        assertEquals(authId, result.authId());
        
        verify(adminRepository).findByAuthIdWithAuthAndState(authId);
    }

    @Test
    @DisplayName("Should throw exception when admin auth id not found")
    void shouldThrowExceptionWhenAdminAuthIdNotFound() {
        // Given
        Long authId = 999L;
        when(adminRepository.findByAuthIdWithAuthAndState(authId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> adminService.getAdminByAuthId(authId));
        
        verify(adminRepository).findByAuthIdWithAuthAndState(authId);
    }

    @Test
    @DisplayName("Should soft delete admin successfully")
    void shouldSoftDeleteAdminSuccessfully() {
        // Given
        Long adminId = 1L;
        when(adminRepository.findByIdWithAuthAndState(adminId)).thenReturn(Optional.of(testAdmin));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(adminRepository.save(any(Admin.class))).thenReturn(testAdmin);

        // When
        adminService.deleteAdmin(adminId);

        // Then
        verify(adminRepository).findByIdWithAuthAndState(adminId);
        verify(stateService).getEliminatedState();
        verify(adminRepository).save(testAdmin);
        assertEquals(eliminatedState, testAdmin.getState());
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing admin")
    void shouldThrowExceptionWhenDeletingNonExistingAdmin() {
        // Given
        Long adminId = 999L;
        when(adminRepository.findByIdWithAuthAndState(adminId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> adminService.deleteAdmin(adminId));
        
        verify(adminRepository).findByIdWithAuthAndState(adminId);
        verify(stateService, never()).getEliminatedState();
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should check if admin is active by auth id successfully")
    void shouldCheckIfAdminIsActiveByAuthIdSuccessfully() {
        // Given
        Long authId = 1L;
        testAdmin.getState().setId(1L); // Active state
        when(adminRepository.findByAuthId(authId)).thenReturn(Optional.of(testAdmin));

        // When
        boolean result = adminService.isActiveAdminByAuthId(authId);

        // Then
        assertTrue(result);
        verify(adminRepository).findByAuthId(authId);
    }

    @Test
    @DisplayName("Should return false when admin not found by auth id")
    void shouldReturnFalseWhenAdminNotFoundByAuthId() {
        // Given
        Long authId = 999L;
        when(adminRepository.findByAuthId(authId)).thenReturn(Optional.empty());

        // When
        boolean result = adminService.isActiveAdminByAuthId(authId);

        // Then
        assertFalse(result);
        verify(adminRepository).findByAuthId(authId);
    }

    @Test
    @DisplayName("Should return false when admin is not active")
    void shouldReturnFalseWhenAdminIsNotActive() {
        // Given
        Long authId = 1L;
        testAdmin.getState().setId(2L); // Not active state (id != 1)
        when(adminRepository.findByAuthId(authId)).thenReturn(Optional.of(testAdmin));

        // When
        boolean result = adminService.isActiveAdminByAuthId(authId);

        // Then
        assertFalse(result);
        verify(adminRepository).findByAuthId(authId);
    }

    @Test
    @DisplayName("Should handle exception when checking admin status")
    void shouldHandleExceptionWhenCheckingAdminStatus() {
        // Given
        Long authId = 1L;
        when(adminRepository.findByAuthId(authId)).thenThrow(new RuntimeException("Database error"));

        // When
        boolean result = adminService.isActiveAdminByAuthId(authId);

        // Then
        assertFalse(result); // Should return false on exception
        verify(adminRepository).findByAuthId(authId);
    }

    @Test
    @DisplayName("Should implement findById method correctly")
    void shouldImplementFindByIdMethodCorrectly() {
        // Given
        Long adminId = 1L;
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(testAdmin));

        // When
        Admin result = adminService.findById(adminId);

        // Then
        assertNotNull(result);
        assertEquals(testAdmin, result);
        verify(adminRepository).findById(adminId);
    }

    @Test
    @DisplayName("Should throw exception in findById when admin not found")
    void shouldThrowExceptionInFindByIdWhenAdminNotFound() {
        // Given
        Long adminId = 999L;
        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> adminService.findById(adminId));
        verify(adminRepository).findById(adminId);
    }

    @Test
    @DisplayName("Should implement findByIdAndStateId method correctly")
    void shouldImplementFindByIdAndStateIdMethodCorrectly() {
        // Given
        Long adminId = 1L;
        Long stateId = 1L;
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(testAdmin));

        // When
        Admin result = adminService.findByIdAndStateId(adminId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testAdmin, result);
        verify(adminRepository).findById(adminId);
    }

    @Test
    @DisplayName("Should throw exception in findByIdAndStateId when states don't match")
    void shouldThrowExceptionInFindByIdAndStateIdWhenStatesDontMatch() {
        // Given
        Long adminId = 1L;
        Long stateId = 2L; // Different from admin's state (1L)
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(testAdmin));

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> adminService.findByIdAndStateId(adminId, stateId));
        
        verify(adminRepository).findById(adminId);
    }

    @Test
    @DisplayName("Should use default state in findByIdAndStateId when stateId is null")
    void shouldUseDefaultStateInFindByIdAndStateIdWhenStateIdIsNull() {
        // Given
        Long adminId = 1L;
        Long stateId = null;
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(testAdmin));

        // When
        Admin result = adminService.findByIdAndStateId(adminId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testAdmin, result);
        verify(adminRepository).findById(adminId);
    }

    @Test
    @DisplayName("Should handle null auth in update process")
    void shouldHandleNullAuthInUpdateProcess() {
        // Given
        Long adminId = 1L;
        testAdmin.setAuth(null); // Simulate null auth
        when(adminRepository.findByIdWithAuthAndState(adminId)).thenReturn(Optional.of(testAdmin));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(adminRepository.save(any(Admin.class))).thenReturn(testAdmin);

        // When
        AdminResponse result = adminService.updateAdmin(adminId, testAdminDto);

        // Then
        assertNotNull(result);
        verify(authEntityCreationService, never()).updateAuthForEntity(any(), any(), any(), any(), any(), any());
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should map to response correctly")
    void shouldMapToResponseCorrectly() {
        // Given - using the testAdmin with all required fields
        testAdmin.setCreatedAt(java.time.LocalDateTime.now());
        testAdmin.setUpdatedAt(java.time.LocalDateTime.now());

        // When
        AdminResponse result = adminService.mapToResponse(testAdmin);

        // Then
        assertEquals(testAdmin.getId(), result.id());
        assertEquals(testAdmin.getEmail(), result.email());
        assertEquals(testAdmin.getName(), result.name());
        assertEquals(testAdmin.getImg(), result.img());
        assertEquals(testAdmin.getAuth().getId(), result.authId());
        assertEquals(testAdmin.getAuth().getUsername(), result.username());
        assertEquals(testAdmin.getState().getId(), result.stateId());
        assertEquals(testAdmin.getState().getState(), result.state());
        assertEquals(testAdmin.getCreatedAt(), result.createdAt());
        assertEquals(testAdmin.getUpdatedAt(), result.updatedAt());
    }
}