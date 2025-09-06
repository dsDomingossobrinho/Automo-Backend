package com.automo.area.controller;

import com.automo.area.dto.AreaDto;
import com.automo.area.response.AreaResponse;
import com.automo.area.service.AreaService;
import com.automo.test.config.BaseTestConfig;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AreaController.class)
@DisplayName("Tests for AreaController")
class AreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AreaService areaService;

    private AreaResponse areaResponse;
    private AreaDto areaDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        areaResponse = new AreaResponse(
                1L,
                "Lisboa Centro",
                "Central area of Lisboa",
                1L,
                "ACTIVE",
                now,
                now
        );
        
        areaDto = TestDataFactory.createValidAreaDto(1L);
    }

    @Test
    @DisplayName("Should create area successfully")
    void shouldCreateAreaSuccessfully() throws Exception {
        // Given
        when(areaService.createArea(any(AreaDto.class))).thenReturn(areaResponse);
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.area").value("Lisboa Centro"))
                .andExpect(jsonPath("$.description").value("Central area of Lisboa"))
                .andExpect(jsonPath("$.stateId").value(1L))
                .andExpect(jsonPath("$.state").value("ACTIVE"));
        
        verify(areaService).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid area data")
    void shouldReturnBadRequestForInvalidAreaData() throws Exception {
        // Given
        AreaDto invalidDto = new AreaDto("", "Description", null);
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(areaService, never()).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should update area successfully")
    void shouldUpdateAreaSuccessfully() throws Exception {
        // Given
        AreaResponse updatedResponse = new AreaResponse(
                1L, "Cascais", "Updated description", 1L, "ACTIVE", 
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(areaService.updateArea(anyLong(), any(AreaDto.class))).thenReturn(updatedResponse);
        
        // When & Then
        mockMvc.perform(put("/api/areas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.area").value("Cascais"))
                .andExpect(jsonPath("$.description").value("Updated description"));
        
        verify(areaService).updateArea(eq(1L), any(AreaDto.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent area")
    void shouldReturnNotFoundWhenUpdatingNonExistentArea() throws Exception {
        // Given
        when(areaService.updateArea(anyLong(), any(AreaDto.class)))
                .thenThrow(new EntityNotFoundException("Area not found"));
        
        // When & Then
        mockMvc.perform(put("/api/areas/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaDto)))
                .andExpect(status().isNotFound());
        
        verify(areaService).updateArea(eq(999L), any(AreaDto.class));
    }

    @Test
    @DisplayName("Should get all areas successfully")
    void shouldGetAllAreasSuccessfully() throws Exception {
        // Given
        AreaResponse area2 = new AreaResponse(
                2L, "Cascais", "Coastal area", 1L, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        List<AreaResponse> areas = Arrays.asList(areaResponse, area2);
        when(areaService.getAllAreas()).thenReturn(areas);
        
        // When & Then
        mockMvc.perform(get("/api/areas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].area").value("Lisboa Centro"))
                .andExpect(jsonPath("$[1].area").value("Cascais"));
        
        verify(areaService).getAllAreas();
    }

    @Test
    @DisplayName("Should get empty list when no areas exist")
    void shouldGetEmptyListWhenNoAreasExist() throws Exception {
        // Given
        when(areaService.getAllAreas()).thenReturn(Arrays.asList());
        
        // When & Then
        mockMvc.perform(get("/api/areas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));
        
        verify(areaService).getAllAreas();
    }

    @Test
    @DisplayName("Should get area by ID successfully")
    void shouldGetAreaByIdSuccessfully() throws Exception {
        // Given
        when(areaService.getAreaByIdResponse(1L)).thenReturn(areaResponse);
        
        // When & Then
        mockMvc.perform(get("/api/areas/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.area").value("Lisboa Centro"))
                .andExpect(jsonPath("$.description").value("Central area of Lisboa"));
        
        verify(areaService).getAreaByIdResponse(1L);
    }

    @Test
    @DisplayName("Should return not found when getting non-existent area")
    void shouldReturnNotFoundWhenGettingNonExistentArea() throws Exception {
        // Given
        when(areaService.getAreaByIdResponse(999L))
                .thenThrow(new EntityNotFoundException("Area not found"));
        
        // When & Then
        mockMvc.perform(get("/api/areas/999"))
                .andExpect(status().isNotFound());
        
        verify(areaService).getAreaByIdResponse(999L);
    }

    @Test
    @DisplayName("Should get areas by state successfully")
    void shouldGetAreasByStateSuccessfully() throws Exception {
        // Given
        List<AreaResponse> areas = Arrays.asList(areaResponse);
        when(areaService.getAreasByState(1L)).thenReturn(areas);
        
        // When & Then
        mockMvc.perform(get("/api/areas/state/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].area").value("Lisboa Centro"));
        
        verify(areaService).getAreasByState(1L);
    }

    @Test
    @DisplayName("Should delete area successfully")
    void shouldDeleteAreaSuccessfully() throws Exception {
        // Given
        doNothing().when(areaService).deleteArea(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/areas/1"))
                .andExpect(status().isNoContent());
        
        verify(areaService).deleteArea(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent area")
    void shouldReturnNotFoundWhenDeletingNonExistentArea() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Area not found"))
                .when(areaService).deleteArea(999L);
        
        // When & Then
        mockMvc.perform(delete("/api/areas/999"))
                .andExpect(status().isNotFound());
        
        verify(areaService).deleteArea(999L);
    }

    @Test
    @DisplayName("Should handle validation errors")
    void shouldHandleValidationErrors() throws Exception {
        // Given - Missing required fields
        String invalidJson = "{}";
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
        
        verify(areaService, never()).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void shouldHandleMalformedJson() throws Exception {
        // Given
        String malformedJson = "{invalid json}";
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
        
        verify(areaService, never()).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should handle service exceptions")
    void shouldHandleServiceExceptions() throws Exception {
        // Given
        when(areaService.createArea(any(AreaDto.class)))
                .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaDto)))
                .andExpect(status().isInternalServerError());
        
        verify(areaService).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should validate area name is not blank")
    void shouldValidateAreaNameIsNotBlank() throws Exception {
        // Given
        AreaDto invalidDto = TestDataFactory.createValidAreaDto("", "Description", 1L);
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(areaService, never()).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should validate state ID is required")
    void shouldValidateStateIdIsRequired() throws Exception {
        // Given
        AreaDto invalidDto = TestDataFactory.createValidAreaDto("Area Name", "Description", null);
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(areaService, never()).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should handle state not found exception")
    void shouldHandleStateNotFoundException() throws Exception {
        // Given
        when(areaService.createArea(any(AreaDto.class)))
                .thenThrow(new EntityNotFoundException("State not found"));
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaDto)))
                .andExpect(status().isNotFound());
        
        verify(areaService).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should return empty list for state with no areas")
    void shouldReturnEmptyListForStateWithNoAreas() throws Exception {
        // Given
        when(areaService.getAreasByState(999L)).thenReturn(Arrays.asList());
        
        // When & Then
        mockMvc.perform(get("/api/areas/state/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));
        
        verify(areaService).getAreasByState(999L);
    }

    @Test
    @DisplayName("Should create area with null description")
    void shouldCreateAreaWithNullDescription() throws Exception {
        // Given
        AreaDto dtoWithNullDescription = TestDataFactory.createValidAreaDto("Test Area", null, 1L);
        
        AreaResponse responseWithNullDescription = new AreaResponse(
                1L, "Test Area", null, 1L, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(areaService.createArea(any(AreaDto.class))).thenReturn(responseWithNullDescription);
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithNullDescription)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.area").value("Test Area"))
                .andExpect(jsonPath("$.description").isEmpty());
        
        verify(areaService).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should create area with empty description")
    void shouldCreateAreaWithEmptyDescription() throws Exception {
        // Given
        AreaDto dtoWithEmptyDescription = TestDataFactory.createValidAreaDto("Test Area", "", 1L);
        
        AreaResponse responseWithEmptyDescription = new AreaResponse(
                1L, "Test Area", "", 1L, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(areaService.createArea(any(AreaDto.class))).thenReturn(responseWithEmptyDescription);
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithEmptyDescription)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.area").value("Test Area"))
                .andExpect(jsonPath("$.description").value(""));
        
        verify(areaService).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should handle areas with long descriptions")
    void shouldHandleAreasWithLongDescriptions() throws Exception {
        // Given
        String longDescription = "This is a very long description that contains multiple sentences. " +
                "It describes the area in great detail, including its history, geography, " +
                "cultural significance, and modern development.";
        
        AreaDto dtoWithLongDescription = TestDataFactory.createValidAreaDto("Historic District", longDescription, 1L);
        
        AreaResponse responseWithLongDescription = new AreaResponse(
                1L, "Historic District", longDescription, 1L, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(areaService.createArea(any(AreaDto.class))).thenReturn(responseWithLongDescription);
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithLongDescription)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.area").value("Historic District"))
                .andExpect(jsonPath("$.description").value(longDescription));
        
        verify(areaService).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should handle areas with special characters")
    void shouldHandleAreasWithSpecialCharacters() throws Exception {
        // Given
        AreaDto dtoWithSpecialChars = TestDataFactory.createValidAreaDto("São João do Estoril", "Area with special chars", 1L);
        
        AreaResponse responseWithSpecialChars = new AreaResponse(
                1L, "São João do Estoril", "Area with special chars", 1L, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(areaService.createArea(any(AreaDto.class))).thenReturn(responseWithSpecialChars);
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithSpecialChars)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.area").value("São João do Estoril"))
                .andExpect(jsonPath("$.description").value("Area with special chars"));
        
        verify(areaService).createArea(any(AreaDto.class));
    }

    @Test
    @DisplayName("Should validate whitespace-only area name as invalid")
    void shouldValidateWhitespaceOnlyAreaNameAsInvalid() throws Exception {
        // Given
        AreaDto invalidDto = TestDataFactory.createValidAreaDto("   ", "Description", 1L);
        
        // When & Then
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(areaService, never()).createArea(any(AreaDto.class));
    }
}