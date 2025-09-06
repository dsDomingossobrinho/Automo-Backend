package com.automo.authRoles.controller;

import com.automo.authRoles.dto.AuthRolesDto;
import com.automo.authRoles.response.AuthRolesResponse;
import com.automo.authRoles.service.AuthRolesService;
import com.automo.test.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive controller tests for AuthRoles REST endpoints
 * Testing HTTP requests, responses, validation, and error handling
 */
@WebMvcTest(AuthRolesController.class)
@DisplayName("AuthRoles Controller Tests")
class AuthRolesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthRolesService authRolesService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRolesDto testAuthRolesDto;
    private AuthRolesResponse testAuthRolesResponse;

    @BeforeEach
    void setUp() {
        testAuthRolesDto = TestDataFactory.createValidAuthRolesDto(1L, 1L, 1L);
        
        testAuthRolesResponse = new AuthRolesResponse(
            1L,
            1L,
            "test@automo.com",
            "testuser",
            1L,
            "USER",
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should create AuthRoles successfully")
    @WithMockUser
    void shouldCreateAuthRolesSuccessfully() throws Exception {
        // Given
        when(authRolesService.createAuthRoles(any(AuthRolesDto.class))).thenReturn(testAuthRolesResponse);

        // When & Then
        mockMvc.perform(post("/auth-roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAuthRolesDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.authId").value(1L))
                .andExpect(jsonPath("$.roleId").value(1L))
                .andExpect(jsonPath("$.stateId").value(1L))
                .andExpect(jsonPath("$.authEmail").value("test@automo.com"))
                .andExpect(jsonPath("$.roleRole").value("USER"))
                .andExpect(jsonPath("$.stateState").value("ACTIVE"));

        verify(authRolesService).createAuthRoles(any(AuthRolesDto.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid AuthRoles data")
    @WithMockUser
    void shouldReturnBadRequestForInvalidAuthRolesData() throws Exception {
        // Given - Invalid DTO with null values
        AuthRolesDto invalidDto = new AuthRolesDto(null, null, null);

        // When & Then
        mockMvc.perform(post("/auth-roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(authRolesService, never()).createAuthRoles(any());
    }

    @Test
    @DisplayName("Should get AuthRoles by ID successfully")
    @WithMockUser
    void shouldGetAuthRolesByIdSuccessfully() throws Exception {
        // Given
        when(authRolesService.getAuthRolesById(1L)).thenReturn(testAuthRolesResponse);

        // When & Then
        mockMvc.perform(get("/auth-roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.authId").value(1L))
                .andExpect(jsonPath("$.roleId").value(1L))
                .andExpect(jsonPath("$.authEmail").value("test@automo.com"));

        verify(authRolesService).getAuthRolesById(1L);
    }

    @Test
    @DisplayName("Should return not found when AuthRoles doesn't exist")
    @WithMockUser
    void shouldReturnNotFoundWhenAuthRolesDoesntExist() throws Exception {
        // Given
        when(authRolesService.getAuthRolesById(999L))
            .thenThrow(new EntityNotFoundException("AuthRoles with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/auth-roles/999"))
                .andExpect(status().isNotFound());

        verify(authRolesService).getAuthRolesById(999L);
    }

    @Test
    @DisplayName("Should get all AuthRoles successfully")
    @WithMockUser
    void shouldGetAllAuthRolesSuccessfully() throws Exception {
        // Given
        List<AuthRolesResponse> responses = Arrays.asList(testAuthRolesResponse);
        when(authRolesService.getAllAuthRoles()).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/auth-roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].authEmail").value("test@automo.com"));

        verify(authRolesService).getAllAuthRoles();
    }

    @Test
    @DisplayName("Should return empty list when no AuthRoles exist")
    @WithMockUser
    void shouldReturnEmptyListWhenNoAuthRolesExist() throws Exception {
        // Given
        when(authRolesService.getAllAuthRoles()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/auth-roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(authRolesService).getAllAuthRoles();
    }

    @Test
    @DisplayName("Should update AuthRoles successfully")
    @WithMockUser
    void shouldUpdateAuthRolesSuccessfully() throws Exception {
        // Given
        AuthRolesResponse updatedResponse = new AuthRolesResponse(
            1L, 1L, "updated@automo.com", "updateduser", 2L, "ADMIN", 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(authRolesService.updateAuthRoles(eq(1L), any(AuthRolesDto.class)))
            .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/auth-roles/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAuthRolesDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.roleId").value(2L))
                .andExpect(jsonPath("$.roleRole").value("ADMIN"));

        verify(authRolesService).updateAuthRoles(eq(1L), any(AuthRolesDto.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent AuthRoles")
    @WithMockUser
    void shouldReturnNotFoundWhenUpdatingNonExistentAuthRoles() throws Exception {
        // Given
        when(authRolesService.updateAuthRoles(eq(999L), any(AuthRolesDto.class)))
            .thenThrow(new EntityNotFoundException("AuthRoles with ID 999 not found"));

        // When & Then
        mockMvc.perform(put("/auth-roles/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAuthRolesDto)))
                .andExpect(status().isNotFound());

        verify(authRolesService).updateAuthRoles(eq(999L), any(AuthRolesDto.class));
    }

    @Test
    @DisplayName("Should delete AuthRoles successfully")
    @WithMockUser
    void shouldDeleteAuthRolesSuccessfully() throws Exception {
        // Given
        doNothing().when(authRolesService).deleteAuthRoles(1L);

        // When & Then
        mockMvc.perform(delete("/auth-roles/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(authRolesService).deleteAuthRoles(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent AuthRoles")
    @WithMockUser
    void shouldReturnNotFoundWhenDeletingNonExistentAuthRoles() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("AuthRoles with ID 999 not found"))
            .when(authRolesService).deleteAuthRoles(999L);

        // When & Then
        mockMvc.perform(delete("/auth-roles/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(authRolesService).deleteAuthRoles(999L);
    }

    @Test
    @DisplayName("Should get AuthRoles by Auth ID successfully")
    @WithMockUser
    void shouldGetAuthRolesByAuthIdSuccessfully() throws Exception {
        // Given
        List<AuthRolesResponse> responses = Arrays.asList(testAuthRolesResponse);
        when(authRolesService.getAuthRolesByAuthId(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/auth-roles/auth/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].authId").value(1L))
                .andExpect(jsonPath("$[0].authEmail").value("test@automo.com"));

        verify(authRolesService).getAuthRolesByAuthId(1L);
    }

    @Test
    @DisplayName("Should get AuthRoles by Role ID successfully")
    @WithMockUser
    void shouldGetAuthRolesByRoleIdSuccessfully() throws Exception {
        // Given
        List<AuthRolesResponse> responses = Arrays.asList(testAuthRolesResponse);
        when(authRolesService.getAuthRolesByRoleId(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/auth-roles/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].roleId").value(1L))
                .andExpect(jsonPath("$[0].roleRole").value("USER"));

        verify(authRolesService).getAuthRolesByRoleId(1L);
    }

    @Test
    @DisplayName("Should get AuthRoles by State ID successfully")
    @WithMockUser
    void shouldGetAuthRolesByStateIdSuccessfully() throws Exception {
        // Given
        List<AuthRolesResponse> responses = Arrays.asList(testAuthRolesResponse);
        when(authRolesService.getAuthRolesByStateId(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/auth-roles/state/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].stateId").value(1L))
                .andExpect(jsonPath("$[0].stateState").value("ACTIVE"));

        verify(authRolesService).getAuthRolesByStateId(1L);
    }

    @Test
    @DisplayName("Should handle runtime exceptions during creation")
    @WithMockUser
    void shouldHandleRuntimeExceptionsDuringCreation() throws Exception {
        // Given
        when(authRolesService.createAuthRoles(any(AuthRolesDto.class)))
            .thenThrow(new RuntimeException("Auth already has this role assigned"));

        // When & Then
        mockMvc.perform(post("/auth-roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAuthRolesDto)))
                .andExpect(status().isInternalServerError());

        verify(authRolesService).createAuthRoles(any(AuthRolesDto.class));
    }

    @Test
    @DisplayName("Should handle runtime exceptions during update")
    @WithMockUser
    void shouldHandleRuntimeExceptionsDuringUpdate() throws Exception {
        // Given
        when(authRolesService.updateAuthRoles(eq(1L), any(AuthRolesDto.class)))
            .thenThrow(new RuntimeException("Auth already has this role assigned"));

        // When & Then
        mockMvc.perform(put("/auth-roles/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAuthRolesDto)))
                .andExpect(status().isInternalServerError());

        verify(authRolesService).updateAuthRoles(eq(1L), any(AuthRolesDto.class));
    }

    @Test
    @DisplayName("Should require authentication for all endpoints")
    void shouldRequireAuthenticationForAllEndpoints() throws Exception {
        // Test POST without authentication
        mockMvc.perform(post("/auth-roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAuthRolesDto)))
                .andExpect(status().isUnauthorized());

        // Test GET without authentication
        mockMvc.perform(get("/auth-roles"))
                .andExpect(status().isUnauthorized());

        // Test PUT without authentication
        mockMvc.perform(put("/auth-roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAuthRolesDto)))
                .andExpect(status().isUnauthorized());

        // Test DELETE without authentication
        mockMvc.perform(delete("/auth-roles/1"))
                .andExpect(status().isUnauthorized());

        verify(authRolesService, never()).createAuthRoles(any());
        verify(authRolesService, never()).getAllAuthRoles();
        verify(authRolesService, never()).updateAuthRoles(any(), any());
        verify(authRolesService, never()).deleteAuthRoles(any());
    }

    @Test
    @DisplayName("Should validate request body for POST requests")
    @WithMockUser
    void shouldValidateRequestBodyForPostRequests() throws Exception {
        // Test with empty body
        mockMvc.perform(post("/auth-roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        // Test with malformed JSON
        mockMvc.perform(post("/auth-roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(authRolesService, never()).createAuthRoles(any());
    }

    @Test
    @DisplayName("Should validate request body for PUT requests")
    @WithMockUser
    void shouldValidateRequestBodyForPutRequests() throws Exception {
        // Test with empty body
        mockMvc.perform(put("/auth-roles/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        // Test with malformed JSON
        mockMvc.perform(put("/auth-roles/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(authRolesService, never()).updateAuthRoles(any(), any());
    }

    @Test
    @DisplayName("Should handle invalid path parameters")
    @WithMockUser
    void shouldHandleInvalidPathParameters() throws Exception {
        // Test with invalid ID format
        mockMvc.perform(get("/auth-roles/invalid-id"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/auth-roles/auth/invalid-id"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/auth-roles/role/invalid-id"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/auth-roles/state/invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should test CORS and security headers")
    @WithMockUser
    void shouldTestCorsAndSecurityHeaders() throws Exception {
        // Given
        when(authRolesService.getAllAuthRoles()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/auth-roles"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().exists("X-XSS-Protection"));

        verify(authRolesService).getAllAuthRoles();
    }
}