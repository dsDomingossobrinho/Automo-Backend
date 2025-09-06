package com.automo.identifier.controller;

import com.automo.identifier.dto.IdentifierDto;
import com.automo.identifier.response.IdentifierResponse;
import com.automo.identifier.service.IdentifierService;
import com.automo.config.security.JwtUtils;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IdentifierController.class)
@ActiveProfiles("test")
@DisplayName("Tests for IdentifierController")
class IdentifierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdentifierService identifierService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private IdentifierDto identifierDto;
    private IdentifierResponse identifierResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        identifierDto = TestDataFactory.createValidIdentifierDto(1L, 1L, 1L);
        
        identifierResponse = new IdentifierResponse(
            1L,
            1L,
            "Test User",
            1L,
            "NIF",
            1L,
            "ACTIVE",
            now,
            now
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all identifiers successfully")
    void shouldGetAllIdentifiersSuccessfully() throws Exception {
        // Given
        List<IdentifierResponse> identifiers = Arrays.asList(identifierResponse);
        when(identifierService.getAllIdentifiers()).thenReturn(identifiers);

        // When & Then
        mockMvc.perform(get("/identifiers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].userName").value("Test User"))
                .andExpect(jsonPath("$[0].identifierTypeId").value(1))
                .andExpect(jsonPath("$[0].identifierType").value("NIF"))
                .andExpect(jsonPath("$[0].stateId").value(1))
                .andExpect(jsonPath("$[0].state").value("ACTIVE"));

        verify(identifierService).getAllIdentifiers();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all identifiers as regular user")
    void shouldGetAllIdentifiersAsRegularUser() throws Exception {
        // Given
        List<IdentifierResponse> identifiers = Arrays.asList(identifierResponse);
        when(identifierService.getAllIdentifiers()).thenReturn(identifiers);

        // When & Then
        mockMvc.perform(get("/identifiers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(identifierService).getAllIdentifiers();
    }

    @Test
    @DisplayName("Should return 401 when accessing without authentication")
    void shouldReturn401WhenAccessingWithoutAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/identifiers"))
                .andExpect(status().isUnauthorized());

        verify(identifierService, never()).getAllIdentifiers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when no identifiers exist")
    void shouldReturnEmptyListWhenNoIdentifiersExist() throws Exception {
        // Given
        when(identifierService.getAllIdentifiers()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/identifiers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(identifierService).getAllIdentifiers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get identifier by ID successfully")
    void shouldGetIdentifierByIdSuccessfully() throws Exception {
        // Given
        when(identifierService.getIdentifierByIdResponse(1L)).thenReturn(identifierResponse);

        // When & Then
        mockMvc.perform(get("/identifiers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.userName").value("Test User"))
                .andExpect(jsonPath("$.identifierType").value("NIF"));

        verify(identifierService).getIdentifierByIdResponse(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when identifier not found by ID")
    void shouldReturn404WhenIdentifierNotFoundById() throws Exception {
        // Given
        when(identifierService.getIdentifierByIdResponse(999L))
                .thenThrow(new EntityNotFoundException("Identifier with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/identifiers/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(identifierService).getIdentifierByIdResponse(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create identifier successfully")
    void shouldCreateIdentifierSuccessfully() throws Exception {
        // Given
        when(identifierService.createIdentifier(any(IdentifierDto.class))).thenReturn(identifierResponse);

        // When & Then
        mockMvc.perform(post("/identifiers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.identifierType").value("NIF"));

        verify(identifierService).createIdentifier(any(IdentifierDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should create identifier as regular user")
    void shouldCreateIdentifierAsRegularUser() throws Exception {
        // Given
        when(identifierService.createIdentifier(any(IdentifierDto.class))).thenReturn(identifierResponse);

        // When & Then
        mockMvc.perform(post("/identifiers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierDto)))
                .andExpect(status().isCreated());

        verify(identifierService).createIdentifier(any(IdentifierDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when creating identifier with invalid data")
    void shouldReturn400WhenCreatingIdentifierWithInvalidData() throws Exception {
        // Given - DTO with null userId
        IdentifierDto invalidDto = TestDataFactory.createValidIdentifierDto(null, 1L, 1L);

        // When & Then
        mockMvc.perform(post("/identifiers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(identifierService, never()).createIdentifier(any(IdentifierDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when creating identifier with missing fields")
    void shouldReturn400WhenCreatingIdentifierWithMissingFields() throws Exception {
        // Given - DTO with null identifierTypeId
        IdentifierDto invalidDto = TestDataFactory.createValidIdentifierDto(1L, null, 1L);

        // When & Then
        mockMvc.perform(post("/identifiers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(identifierService, never()).createIdentifier(any(IdentifierDto.class));
    }

    @Test
    @DisplayName("Should return 403 when creating identifier without CSRF token")
    void shouldReturn403WhenCreatingIdentifierWithoutCsrfToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/identifiers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierDto)))
                .andExpect(status().isForbidden());

        verify(identifierService, never()).createIdentifier(any(IdentifierDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update identifier successfully")
    void shouldUpdateIdentifierSuccessfully() throws Exception {
        // Given
        when(identifierService.updateIdentifier(eq(1L), any(IdentifierDto.class))).thenReturn(identifierResponse);

        // When & Then
        mockMvc.perform(put("/identifiers/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));

        verify(identifierService).updateIdentifier(eq(1L), any(IdentifierDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when updating non-existent identifier")
    void shouldReturn404WhenUpdatingNonExistentIdentifier() throws Exception {
        // Given
        when(identifierService.updateIdentifier(eq(999L), any(IdentifierDto.class)))
                .thenThrow(new EntityNotFoundException("Identifier with ID 999 not found"));

        // When & Then
        mockMvc.perform(put("/identifiers/{id}", 999L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierDto)))
                .andExpect(status().isNotFound());

        verify(identifierService).updateIdentifier(eq(999L), any(IdentifierDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when updating identifier with invalid data")
    void shouldReturn400WhenUpdatingIdentifierWithInvalidData() throws Exception {
        // Given - DTO with null stateId
        IdentifierDto invalidDto = TestDataFactory.createValidIdentifierDto(1L, 1L, null);

        // When & Then
        mockMvc.perform(put("/identifiers/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(identifierService, never()).updateIdentifier(eq(1L), any(IdentifierDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete identifier successfully")
    void shouldDeleteIdentifierSuccessfully() throws Exception {
        // Given
        doNothing().when(identifierService).deleteIdentifier(1L);

        // When & Then
        mockMvc.perform(delete("/identifiers/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(identifierService).deleteIdentifier(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should delete identifier as regular user")
    void shouldDeleteIdentifierAsRegularUser() throws Exception {
        // Given
        doNothing().when(identifierService).deleteIdentifier(1L);

        // When & Then
        mockMvc.perform(delete("/identifiers/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(identifierService).deleteIdentifier(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when deleting non-existent identifier")
    void shouldReturn404WhenDeletingNonExistentIdentifier() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Identifier with ID 999 not found"))
                .when(identifierService).deleteIdentifier(999L);

        // When & Then
        mockMvc.perform(delete("/identifiers/{id}", 999L)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(identifierService).deleteIdentifier(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get identifiers by state successfully")
    void shouldGetIdentifiersByStateSuccessfully() throws Exception {
        // Given
        List<IdentifierResponse> identifiers = Arrays.asList(identifierResponse);
        when(identifierService.getIdentifiersByState(1L)).thenReturn(identifiers);

        // When & Then
        mockMvc.perform(get("/identifiers/state/{stateId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].stateId").value(1))
                .andExpect(jsonPath("$[0].state").value("ACTIVE"));

        verify(identifierService).getIdentifiersByState(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when no identifiers found by state")
    void shouldReturnEmptyListWhenNoIdentifiersFoundByState() throws Exception {
        // Given
        when(identifierService.getIdentifiersByState(999L)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/identifiers/state/{stateId}", 999L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(identifierService).getIdentifiersByState(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get identifiers by user successfully")
    void shouldGetIdentifiersByUserSuccessfully() throws Exception {
        // Given
        List<IdentifierResponse> identifiers = Arrays.asList(identifierResponse);
        when(identifierService.getIdentifiersByUser(1L)).thenReturn(identifiers);

        // When & Then
        mockMvc.perform(get("/identifiers/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].userName").value("Test User"));

        verify(identifierService).getIdentifiersByUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get identifiers by type successfully")
    void shouldGetIdentifiersByTypeSuccessfully() throws Exception {
        // Given
        List<IdentifierResponse> identifiers = Arrays.asList(identifierResponse);
        when(identifierService.getIdentifiersByType(1L)).thenReturn(identifiers);

        // When & Then
        mockMvc.perform(get("/identifiers/type/{identifierTypeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].identifierTypeId").value(1))
                .andExpect(jsonPath("$[0].identifierType").value("NIF"));

        verify(identifierService).getIdentifiersByType(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should allow regular user to access filtered endpoints")
    void shouldAllowRegularUserToAccessFilteredEndpoints() throws Exception {
        // Given
        List<IdentifierResponse> identifiers = Arrays.asList(identifierResponse);
        when(identifierService.getIdentifiersByUser(1L)).thenReturn(identifiers);

        // When & Then
        mockMvc.perform(get("/identifiers/user/{userId}", 1L))
                .andExpect(status().isOk());

        verify(identifierService).getIdentifiersByUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle invalid path parameters gracefully")
    void shouldHandleInvalidPathParametersGracefully() throws Exception {
        // When & Then - Invalid ID format should be handled by Spring
        mockMvc.perform(get("/identifiers/{id}", "invalid"))
                .andExpect(status().isBadRequest());

        verify(identifierService, never()).getIdentifierByIdResponse(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle malformed JSON in request body")
    void shouldHandleMalformedJsonInRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/identifiers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{malformed json}"))
                .andExpect(status().isBadRequest());

        verify(identifierService, never()).createIdentifier(any(IdentifierDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle empty request body")
    void shouldHandleEmptyRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/identifiers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(identifierService, never()).createIdentifier(any(IdentifierDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exceptions gracefully")
    void shouldHandleServiceExceptionsGracefully() throws Exception {
        // Given
        when(identifierService.getAllIdentifiers()).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(get("/identifiers"))
                .andExpect(status().isInternalServerError());

        verify(identifierService).getAllIdentifiers();
    }
}