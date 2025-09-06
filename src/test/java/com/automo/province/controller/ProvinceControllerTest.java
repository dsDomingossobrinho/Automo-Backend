package com.automo.province.controller;

import com.automo.province.dto.ProvinceDto;
import com.automo.province.response.ProvinceResponse;
import com.automo.province.service.ProvinceService;
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

@WebMvcTest(ProvinceController.class)
@DisplayName("Tests for ProvinceController")
class ProvinceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProvinceService provinceService;

    private ProvinceResponse provinceResponse;
    private ProvinceDto provinceDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        provinceResponse = new ProvinceResponse(
                1L,
                "Lisboa",
                1L,
                "Portugal",
                1L,
                "ACTIVE",
                now,
                now
        );
        
        provinceDto = TestDataFactory.createValidProvinceDto(1L, 1L);
    }

    @Test
    @DisplayName("Should create province successfully")
    void shouldCreateProvinceSuccessfully() throws Exception {
        // Given
        when(provinceService.createProvince(any(ProvinceDto.class))).thenReturn(provinceResponse);
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provinceDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.province").value("Lisboa"))
                .andExpect(jsonPath("$.countryId").value(1L))
                .andExpect(jsonPath("$.countryName").value("Portugal"))
                .andExpect(jsonPath("$.stateId").value(1L))
                .andExpect(jsonPath("$.state").value("ACTIVE"));
        
        verify(provinceService).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid province data")
    void shouldReturnBadRequestForInvalidProvinceData() throws Exception {
        // Given
        ProvinceDto invalidDto = new ProvinceDto("", null, null);
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(provinceService, never()).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should update province successfully")
    void shouldUpdateProvinceSuccessfully() throws Exception {
        // Given
        ProvinceResponse updatedResponse = new ProvinceResponse(
                1L, "Porto", 1L, "Portugal", 1L, "ACTIVE", 
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(provinceService.updateProvince(anyLong(), any(ProvinceDto.class))).thenReturn(updatedResponse);
        
        // When & Then
        mockMvc.perform(put("/api/provinces/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provinceDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.province").value("Porto"));
        
        verify(provinceService).updateProvince(eq(1L), any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent province")
    void shouldReturnNotFoundWhenUpdatingNonExistentProvince() throws Exception {
        // Given
        when(provinceService.updateProvince(anyLong(), any(ProvinceDto.class)))
                .thenThrow(new EntityNotFoundException("Province not found"));
        
        // When & Then
        mockMvc.perform(put("/api/provinces/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provinceDto)))
                .andExpect(status().isNotFound());
        
        verify(provinceService).updateProvince(eq(999L), any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should get all provinces successfully")
    void shouldGetAllProvincesSuccessfully() throws Exception {
        // Given
        ProvinceResponse province2 = new ProvinceResponse(
                2L, "Porto", 1L, "Portugal", 1L, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        List<ProvinceResponse> provinces = Arrays.asList(provinceResponse, province2);
        when(provinceService.getAllProvinces()).thenReturn(provinces);
        
        // When & Then
        mockMvc.perform(get("/api/provinces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].province").value("Lisboa"))
                .andExpect(jsonPath("$[1].province").value("Porto"));
        
        verify(provinceService).getAllProvinces();
    }

    @Test
    @DisplayName("Should get empty list when no provinces exist")
    void shouldGetEmptyListWhenNoProvincesExist() throws Exception {
        // Given
        when(provinceService.getAllProvinces()).thenReturn(Arrays.asList());
        
        // When & Then
        mockMvc.perform(get("/api/provinces"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));
        
        verify(provinceService).getAllProvinces();
    }

    @Test
    @DisplayName("Should get province by ID successfully")
    void shouldGetProvinceByIdSuccessfully() throws Exception {
        // Given
        when(provinceService.getProvinceByIdResponse(1L)).thenReturn(provinceResponse);
        
        // When & Then
        mockMvc.perform(get("/api/provinces/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.province").value("Lisboa"))
                .andExpect(jsonPath("$.countryName").value("Portugal"));
        
        verify(provinceService).getProvinceByIdResponse(1L);
    }

    @Test
    @DisplayName("Should return not found when getting non-existent province")
    void shouldReturnNotFoundWhenGettingNonExistentProvince() throws Exception {
        // Given
        when(provinceService.getProvinceByIdResponse(999L))
                .thenThrow(new EntityNotFoundException("Province not found"));
        
        // When & Then
        mockMvc.perform(get("/api/provinces/999"))
                .andExpect(status().isNotFound());
        
        verify(provinceService).getProvinceByIdResponse(999L);
    }

    @Test
    @DisplayName("Should get provinces by state successfully")
    void shouldGetProvincesByStateSuccessfully() throws Exception {
        // Given
        List<ProvinceResponse> provinces = Arrays.asList(provinceResponse);
        when(provinceService.getProvincesByState(1L)).thenReturn(provinces);
        
        // When & Then
        mockMvc.perform(get("/api/provinces/state/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].province").value("Lisboa"));
        
        verify(provinceService).getProvincesByState(1L);
    }

    @Test
    @DisplayName("Should get provinces by country successfully")
    void shouldGetProvincesByCountrySuccessfully() throws Exception {
        // Given
        ProvinceResponse porto = new ProvinceResponse(
                2L, "Porto", 1L, "Portugal", 1L, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        List<ProvinceResponse> provinces = Arrays.asList(provinceResponse, porto);
        when(provinceService.getProvincesByCountry(1L)).thenReturn(provinces);
        
        // When & Then
        mockMvc.perform(get("/api/provinces/country/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].province").value("Lisboa"))
                .andExpect(jsonPath("$[1].province").value("Porto"));
        
        verify(provinceService).getProvincesByCountry(1L);
    }

    @Test
    @DisplayName("Should delete province successfully")
    void shouldDeleteProvinceSuccessfully() throws Exception {
        // Given
        doNothing().when(provinceService).deleteProvince(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/provinces/1"))
                .andExpect(status().isNoContent());
        
        verify(provinceService).deleteProvince(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent province")
    void shouldReturnNotFoundWhenDeletingNonExistentProvince() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Province not found"))
                .when(provinceService).deleteProvince(999L);
        
        // When & Then
        mockMvc.perform(delete("/api/provinces/999"))
                .andExpect(status().isNotFound());
        
        verify(provinceService).deleteProvince(999L);
    }

    @Test
    @DisplayName("Should handle validation errors")
    void shouldHandleValidationErrors() throws Exception {
        // Given - Missing required fields
        String invalidJson = "{}";
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
        
        verify(provinceService, never()).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void shouldHandleMalformedJson() throws Exception {
        // Given
        String malformedJson = "{invalid json}";
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
        
        verify(provinceService, never()).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should handle service exceptions")
    void shouldHandleServiceExceptions() throws Exception {
        // Given
        when(provinceService.createProvince(any(ProvinceDto.class)))
                .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provinceDto)))
                .andExpect(status().isInternalServerError());
        
        verify(provinceService).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should validate province name is not blank")
    void shouldValidateProvinceNameIsNotBlank() throws Exception {
        // Given
        ProvinceDto invalidDto = TestDataFactory.createValidProvinceDto("", 1L, 1L);
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(provinceService, never()).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should validate country ID is required")
    void shouldValidateCountryIdIsRequired() throws Exception {
        // Given
        ProvinceDto invalidDto = TestDataFactory.createValidProvinceDto("Lisboa", null, 1L);
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(provinceService, never()).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should validate state ID is required")
    void shouldValidateStateIdIsRequired() throws Exception {
        // Given
        ProvinceDto invalidDto = TestDataFactory.createValidProvinceDto("Lisboa", 1L, null);
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(provinceService, never()).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should handle country not found exception")
    void shouldHandleCountryNotFoundException() throws Exception {
        // Given
        when(provinceService.createProvince(any(ProvinceDto.class)))
                .thenThrow(new EntityNotFoundException("Country not found"));
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provinceDto)))
                .andExpect(status().isNotFound());
        
        verify(provinceService).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should handle state not found exception")
    void shouldHandleStateNotFoundException() throws Exception {
        // Given
        when(provinceService.createProvince(any(ProvinceDto.class)))
                .thenThrow(new EntityNotFoundException("State not found"));
        
        // When & Then
        mockMvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provinceDto)))
                .andExpect(status().isNotFound());
        
        verify(provinceService).createProvince(any(ProvinceDto.class));
    }

    @Test
    @DisplayName("Should return empty list for country with no provinces")
    void shouldReturnEmptyListForCountryWithNoProvinces() throws Exception {
        // Given
        when(provinceService.getProvincesByCountry(999L)).thenReturn(Arrays.asList());
        
        // When & Then
        mockMvc.perform(get("/api/provinces/country/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));
        
        verify(provinceService).getProvincesByCountry(999L);
    }

    @Test
    @DisplayName("Should return empty list for state with no provinces")
    void shouldReturnEmptyListForStateWithNoProvinces() throws Exception {
        // Given
        when(provinceService.getProvincesByState(999L)).thenReturn(Arrays.asList());
        
        // When & Then
        mockMvc.perform(get("/api/provinces/state/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));
        
        verify(provinceService).getProvincesByState(999L);
    }
}