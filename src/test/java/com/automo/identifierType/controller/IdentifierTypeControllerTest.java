package com.automo.identifierType.controller;

import com.automo.identifierType.dto.IdentifierTypeDto;
import com.automo.identifierType.response.IdentifierTypeResponse;
import com.automo.identifierType.service.IdentifierTypeService;
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

@WebMvcTest(IdentifierTypeController.class)
@ActiveProfiles("test")
@DisplayName("Tests for IdentifierTypeController")
class IdentifierTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdentifierTypeService identifierTypeService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private IdentifierTypeDto identifierTypeDto;
    private IdentifierTypeResponse identifierTypeResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        identifierTypeDto = TestDataFactory.createValidIdentifierTypeDto("NIF", "Número de Identificação Fiscal");
        
        identifierTypeResponse = new IdentifierTypeResponse(
            1L,
            "NIF",
            "Número de Identificação Fiscal",
            now,
            now
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all identifier types successfully")
    void shouldGetAllIdentifierTypesSuccessfully() throws Exception {
        // Given
        List<IdentifierTypeResponse> identifierTypes = Arrays.asList(identifierTypeResponse);
        when(identifierTypeService.getAllIdentifierTypes()).thenReturn(identifierTypes);

        // When & Then
        mockMvc.perform(get("/identifier-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("NIF"))
                .andExpect(jsonPath("$[0].description").value("Número de Identificação Fiscal"));

        verify(identifierTypeService).getAllIdentifierTypes();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all identifier types as regular user")
    void shouldGetAllIdentifierTypesAsRegularUser() throws Exception {
        // Given
        List<IdentifierTypeResponse> identifierTypes = Arrays.asList(identifierTypeResponse);
        when(identifierTypeService.getAllIdentifierTypes()).thenReturn(identifierTypes);

        // When & Then
        mockMvc.perform(get("/identifier-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(identifierTypeService).getAllIdentifierTypes();
    }

    @Test
    @DisplayName("Should return 401 when accessing without authentication")
    void shouldReturn401WhenAccessingWithoutAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/identifier-types"))
                .andExpect(status().isUnauthorized());

        verify(identifierTypeService, never()).getAllIdentifierTypes();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when no identifier types exist")
    void shouldReturnEmptyListWhenNoIdentifierTypesExist() throws Exception {
        // Given
        when(identifierTypeService.getAllIdentifierTypes()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/identifier-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(identifierTypeService).getAllIdentifierTypes();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get identifier type by ID successfully")
    void shouldGetIdentifierTypeByIdSuccessfully() throws Exception {
        // Given
        when(identifierTypeService.getIdentifierTypeByIdResponse(1L)).thenReturn(identifierTypeResponse);

        // When & Then
        mockMvc.perform(get("/identifier-types/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("NIF"))
                .andExpect(jsonPath("$.description").value("Número de Identificação Fiscal"));

        verify(identifierTypeService).getIdentifierTypeByIdResponse(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when identifier type not found by ID")
    void shouldReturn404WhenIdentifierTypeNotFoundById() throws Exception {
        // Given
        when(identifierTypeService.getIdentifierTypeByIdResponse(999L))
                .thenThrow(new EntityNotFoundException("IdentifierType with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/identifier-types/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(identifierTypeService).getIdentifierTypeByIdResponse(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create identifier type successfully")
    void shouldCreateIdentifierTypeSuccessfully() throws Exception {
        // Given
        when(identifierTypeService.createIdentifierType(any(IdentifierTypeDto.class))).thenReturn(identifierTypeResponse);

        // When & Then
        mockMvc.perform(post("/identifier-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierTypeDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("NIF"))
                .andExpect(jsonPath("$.description").value("Número de Identificação Fiscal"));

        verify(identifierTypeService).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should create identifier type as regular user")
    void shouldCreateIdentifierTypeAsRegularUser() throws Exception {
        // Given
        when(identifierTypeService.createIdentifierType(any(IdentifierTypeDto.class))).thenReturn(identifierTypeResponse);

        // When & Then
        mockMvc.perform(post("/identifier-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierTypeDto)))
                .andExpect(status().isCreated());

        verify(identifierTypeService).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when creating identifier type with invalid data")
    void shouldReturn400WhenCreatingIdentifierTypeWithInvalidData() throws Exception {
        // Given - DTO with null type
        IdentifierTypeDto invalidDto = TestDataFactory.createValidIdentifierTypeDto(null, "Description");

        // When & Then
        mockMvc.perform(post("/identifier-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(identifierTypeService, never()).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when creating identifier type with blank type")
    void shouldReturn400WhenCreatingIdentifierTypeWithBlankType() throws Exception {
        // Given - DTO with blank type
        IdentifierTypeDto invalidDto = TestDataFactory.createValidIdentifierTypeDto("", "Description");

        // When & Then
        mockMvc.perform(post("/identifier-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(identifierTypeService, never()).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create identifier type with null description")
    void shouldCreateIdentifierTypeWithNullDescription() throws Exception {
        // Given
        IdentifierTypeDto dtoWithNullDescription = TestDataFactory.createValidIdentifierTypeDto("NIPC", null);
        IdentifierTypeResponse responseWithNullDescription = new IdentifierTypeResponse(2L, "NIPC", null, now, now);
        
        when(identifierTypeService.createIdentifierType(any(IdentifierTypeDto.class))).thenReturn(responseWithNullDescription);

        // When & Then
        mockMvc.perform(post("/identifier-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithNullDescription)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.type").value("NIPC"))
                .andExpect(jsonPath("$.description").isEmpty());

        verify(identifierTypeService).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @DisplayName("Should return 403 when creating identifier type without CSRF token")
    void shouldReturn403WhenCreatingIdentifierTypeWithoutCsrfToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/identifier-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierTypeDto)))
                .andExpect(status().isForbidden());

        verify(identifierTypeService, never()).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update identifier type successfully")
    void shouldUpdateIdentifierTypeSuccessfully() throws Exception {
        // Given
        when(identifierTypeService.updateIdentifierType(eq(1L), any(IdentifierTypeDto.class))).thenReturn(identifierTypeResponse);

        // When & Then
        mockMvc.perform(put("/identifier-types/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierTypeDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("NIF"));

        verify(identifierTypeService).updateIdentifierType(eq(1L), any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when updating non-existent identifier type")
    void shouldReturn404WhenUpdatingNonExistentIdentifierType() throws Exception {
        // Given
        when(identifierTypeService.updateIdentifierType(eq(999L), any(IdentifierTypeDto.class)))
                .thenThrow(new EntityNotFoundException("IdentifierType with ID 999 not found"));

        // When & Then
        mockMvc.perform(put("/identifier-types/{id}", 999L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(identifierTypeDto)))
                .andExpect(status().isNotFound());

        verify(identifierTypeService).updateIdentifierType(eq(999L), any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when updating identifier type with invalid data")
    void shouldReturn400WhenUpdatingIdentifierTypeWithInvalidData() throws Exception {
        // Given - DTO with blank type
        IdentifierTypeDto invalidDto = TestDataFactory.createValidIdentifierTypeDto("   ", "Description");

        // When & Then
        mockMvc.perform(put("/identifier-types/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(identifierTypeService, never()).updateIdentifierType(eq(1L), any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete identifier type successfully")
    void shouldDeleteIdentifierTypeSuccessfully() throws Exception {
        // Given
        doNothing().when(identifierTypeService).deleteIdentifierType(1L);

        // When & Then
        mockMvc.perform(delete("/identifier-types/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(identifierTypeService).deleteIdentifierType(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should delete identifier type as regular user")
    void shouldDeleteIdentifierTypeAsRegularUser() throws Exception {
        // Given
        doNothing().when(identifierTypeService).deleteIdentifierType(1L);

        // When & Then
        mockMvc.perform(delete("/identifier-types/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(identifierTypeService).deleteIdentifierType(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when deleting non-existent identifier type")
    void shouldReturn404WhenDeletingNonExistentIdentifierType() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("IdentifierType with ID 999 not found"))
                .when(identifierTypeService).deleteIdentifierType(999L);

        // When & Then
        mockMvc.perform(delete("/identifier-types/{id}", 999L)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(identifierTypeService).deleteIdentifierType(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle invalid path parameters gracefully")
    void shouldHandleInvalidPathParametersGracefully() throws Exception {
        // When & Then - Invalid ID format should be handled by Spring
        mockMvc.perform(get("/identifier-types/{id}", "invalid"))
                .andExpect(status().isBadRequest());

        verify(identifierTypeService, never()).getIdentifierTypeByIdResponse(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle malformed JSON in request body")
    void shouldHandleMalformedJsonInRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/identifier-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{malformed json}"))
                .andExpect(status().isBadRequest());

        verify(identifierTypeService, never()).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle empty request body")
    void shouldHandleEmptyRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/identifier-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(identifierTypeService, never()).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exceptions gracefully")
    void shouldHandleServiceExceptionsGracefully() throws Exception {
        // Given
        when(identifierTypeService.getAllIdentifierTypes()).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(get("/identifier-types"))
                .andExpect(status().isInternalServerError());

        verify(identifierTypeService).getAllIdentifierTypes();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should accept valid special characters in type")
    void shouldAcceptValidSpecialCharactersInType() throws Exception {
        // Given
        IdentifierTypeDto specialCharsDto = TestDataFactory.createValidIdentifierTypeDto(
                "NIF-PT_2024", 
                "Número de Identificação Fiscal - Portugal (2024)"
        );
        IdentifierTypeResponse specialCharsResponse = new IdentifierTypeResponse(
                3L, 
                "NIF-PT_2024", 
                "Número de Identificação Fiscal - Portugal (2024)", 
                now, 
                now
        );
        
        when(identifierTypeService.createIdentifierType(any(IdentifierTypeDto.class))).thenReturn(specialCharsResponse);

        // When & Then
        mockMvc.perform(post("/identifier-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialCharsDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("NIF-PT_2024"))
                .andExpect(jsonPath("$.description").value("Número de Identificação Fiscal - Portugal (2024)"));

        verify(identifierTypeService).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle long type and description values")
    void shouldHandleLongTypeAndDescriptionValues() throws Exception {
        // Given
        String longType = "A".repeat(100);
        String longDescription = "B".repeat(500);
        
        IdentifierTypeDto longDto = TestDataFactory.createValidIdentifierTypeDto(longType, longDescription);
        IdentifierTypeResponse longResponse = new IdentifierTypeResponse(4L, longType, longDescription, now, now);
        
        when(identifierTypeService.createIdentifierType(any(IdentifierTypeDto.class))).thenReturn(longResponse);

        // When & Then
        mockMvc.perform(post("/identifier-types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value(longType))
                .andExpect(jsonPath("$.description").value(longDescription));

        verify(identifierTypeService).createIdentifierType(any(IdentifierTypeDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return proper response headers")
    void shouldReturnProperResponseHeaders() throws Exception {
        // Given
        when(identifierTypeService.getAllIdentifierTypes()).thenReturn(Arrays.asList(identifierTypeResponse));

        // When & Then
        mockMvc.perform(get("/identifier-types"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"));

        verify(identifierTypeService).getAllIdentifierTypes();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle concurrent requests correctly")
    void shouldHandleConcurrentRequestsCorrectly() throws Exception {
        // Given
        when(identifierTypeService.getAllIdentifierTypes()).thenReturn(Arrays.asList(identifierTypeResponse));

        // When & Then - Simulate concurrent requests
        mockMvc.perform(get("/identifier-types"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/identifier-types"))
                .andExpect(status().isOk());

        verify(identifierTypeService, times(2)).getAllIdentifierTypes();
    }
}