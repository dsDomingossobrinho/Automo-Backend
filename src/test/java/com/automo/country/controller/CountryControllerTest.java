package com.automo.country.controller;

import com.automo.country.dto.CountryDto;
import com.automo.country.response.CountryResponse;
import com.automo.country.service.CountryService;
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

@WebMvcTest(CountryController.class)
@DisplayName("Tests for CountryController")
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CountryService countryService;

    private CountryResponse countryResponse;
    private CountryDto countryDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        countryResponse = new CountryResponse(
                1L,
                "Portugal",
                9,
                "+351",
                1L,
                "ACTIVE",
                now,
                now
        );
        
        countryDto = TestDataFactory.createValidCountryDto(1L);
    }

    @Test
    @DisplayName("Should create country successfully")
    void shouldCreateCountrySuccessfully() throws Exception {
        // Given
        when(countryService.createCountry(any(CountryDto.class))).thenReturn(countryResponse);
        
        // When & Then
        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(countryDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.country").value("Portugal"))
                .andExpect(jsonPath("$.numberDigits").value(9))
                .andExpect(jsonPath("$.indicative").value("+351"))
                .andExpect(jsonPath("$.stateId").value(1L))
                .andExpect(jsonPath("$.state").value("ACTIVE"));
        
        verify(countryService).createCountry(any(CountryDto.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid country data")
    void shouldReturnBadRequestForInvalidCountryData() throws Exception {
        // Given
        CountryDto invalidDto = new CountryDto("", -1, "", null);
        
        // When & Then
        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(countryService, never()).createCountry(any(CountryDto.class));
    }

    @Test
    @DisplayName("Should update country successfully")
    void shouldUpdateCountrySuccessfully() throws Exception {
        // Given
        CountryResponse updatedResponse = new CountryResponse(
                1L, "Spain", 9, "+34", 1L, "ACTIVE", 
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(countryService.updateCountry(anyLong(), any(CountryDto.class))).thenReturn(updatedResponse);
        
        // When & Then
        mockMvc.perform(put("/api/countries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(countryDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.country").value("Spain"))
                .andExpect(jsonPath("$.indicative").value("+34"));
        
        verify(countryService).updateCountry(eq(1L), any(CountryDto.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent country")
    void shouldReturnNotFoundWhenUpdatingNonExistentCountry() throws Exception {
        // Given
        when(countryService.updateCountry(anyLong(), any(CountryDto.class)))
                .thenThrow(new EntityNotFoundException("Country not found"));
        
        // When & Then
        mockMvc.perform(put("/api/countries/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(countryDto)))
                .andExpect(status().isNotFound());
        
        verify(countryService).updateCountry(eq(999L), any(CountryDto.class));
    }

    @Test
    @DisplayName("Should get all countries successfully")
    void shouldGetAllCountriesSuccessfully() throws Exception {
        // Given
        CountryResponse country2 = new CountryResponse(
                2L, "Spain", 9, "+34", 1L, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );
        
        List<CountryResponse> countries = Arrays.asList(countryResponse, country2);
        when(countryService.getAllCountries()).thenReturn(countries);
        
        // When & Then
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].country").value("Portugal"))
                .andExpect(jsonPath("$[1].country").value("Spain"));
        
        verify(countryService).getAllCountries();
    }

    @Test
    @DisplayName("Should get empty list when no countries exist")
    void shouldGetEmptyListWhenNoCountriesExist() throws Exception {
        // Given
        when(countryService.getAllCountries()).thenReturn(Arrays.asList());
        
        // When & Then
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));
        
        verify(countryService).getAllCountries();
    }

    @Test
    @DisplayName("Should get country by ID successfully")
    void shouldGetCountryByIdSuccessfully() throws Exception {
        // Given
        when(countryService.getCountryByIdResponse(1L)).thenReturn(countryResponse);
        
        // When & Then
        mockMvc.perform(get("/api/countries/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.country").value("Portugal"))
                .andExpect(jsonPath("$.numberDigits").value(9));
        
        verify(countryService).getCountryByIdResponse(1L);
    }

    @Test
    @DisplayName("Should return not found when getting non-existent country")
    void shouldReturnNotFoundWhenGettingNonExistentCountry() throws Exception {
        // Given
        when(countryService.getCountryByIdResponse(999L))
                .thenThrow(new EntityNotFoundException("Country not found"));
        
        // When & Then
        mockMvc.perform(get("/api/countries/999"))
                .andExpect(status().isNotFound());
        
        verify(countryService).getCountryByIdResponse(999L);
    }

    @Test
    @DisplayName("Should get countries by state successfully")
    void shouldGetCountriesByStateSuccessfully() throws Exception {
        // Given
        List<CountryResponse> countries = Arrays.asList(countryResponse);
        when(countryService.getCountriesByState(1L)).thenReturn(countries);
        
        // When & Then
        mockMvc.perform(get("/api/countries/state/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].country").value("Portugal"));
        
        verify(countryService).getCountriesByState(1L);
    }

    @Test
    @DisplayName("Should delete country successfully")
    void shouldDeleteCountrySuccessfully() throws Exception {
        // Given
        doNothing().when(countryService).deleteCountry(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/countries/1"))
                .andExpect(status().isNoContent());
        
        verify(countryService).deleteCountry(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent country")
    void shouldReturnNotFoundWhenDeletingNonExistentCountry() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Country not found"))
                .when(countryService).deleteCountry(999L);
        
        // When & Then
        mockMvc.perform(delete("/api/countries/999"))
                .andExpect(status().isNotFound());
        
        verify(countryService).deleteCountry(999L);
    }

    @Test
    @DisplayName("Should handle validation errors")
    void shouldHandleValidationErrors() throws Exception {
        // Given - Missing required fields
        String invalidJson = "{}";
        
        // When & Then
        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
        
        verify(countryService, never()).createCountry(any(CountryDto.class));
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void shouldHandleMalformedJson() throws Exception {
        // Given
        String malformedJson = "{invalid json}";
        
        // When & Then
        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
        
        verify(countryService, never()).createCountry(any(CountryDto.class));
    }

    @Test
    @DisplayName("Should handle service exceptions")
    void shouldHandleServiceExceptions() throws Exception {
        // Given
        when(countryService.createCountry(any(CountryDto.class)))
                .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(countryDto)))
                .andExpect(status().isInternalServerError());
        
        verify(countryService).createCountry(any(CountryDto.class));
    }

    @Test
    @DisplayName("Should validate country name is not blank")
    void shouldValidateCountryNameIsNotBlank() throws Exception {
        // Given
        CountryDto invalidDto = TestDataFactory.createValidCountryDto("", 9, "+351", 1L);
        
        // When & Then
        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(countryService, never()).createCountry(any(CountryDto.class));
    }

    @Test
    @DisplayName("Should validate number digits is positive")
    void shouldValidateNumberDigitsIsPositive() throws Exception {
        // Given
        CountryDto invalidDto = TestDataFactory.createValidCountryDto("Portugal", -1, "+351", 1L);
        
        // When & Then
        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        
        verify(countryService, never()).createCountry(any(CountryDto.class));
    }
}