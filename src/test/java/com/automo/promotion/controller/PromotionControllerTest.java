package com.automo.promotion.controller;

import com.automo.promotion.dto.PromotionDto;
import com.automo.promotion.response.PromotionResponse;
import com.automo.promotion.service.PromotionService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromotionController.class)
@DisplayName("Tests for PromotionController")
class PromotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PromotionService promotionService;

    private PromotionResponse promotionResponse;
    private PromotionDto promotionDto;

    @BeforeEach
    void setUp() {
        promotionResponse = new PromotionResponse(
            1L,
            "Summer Sale",
            new BigDecimal("20.00"),
            "SUMMER20",
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        promotionDto = TestDataFactory.createValidPromotionDto(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should get all promotions")
    void shouldGetAllPromotions() throws Exception {
        // Given
        List<PromotionResponse> responses = Arrays.asList(
            promotionResponse,
            new PromotionResponse(2L, "Black Friday", new BigDecimal("50.00"), "BF50", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now())
        );
        
        when(promotionService.getAllPromotions()).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/promotions"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("Summer Sale"))
            .andExpect(jsonPath("$[0].discountValue").value(20.00))
            .andExpect(jsonPath("$[0].code").value("SUMMER20"))
            .andExpect(jsonPath("$[0].stateId").value(1L))
            .andExpect(jsonPath("$[0].stateName").value("ACTIVE"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].name").value("Black Friday"))
            .andExpect(jsonPath("$[1].discountValue").value(50.00))
            .andExpect(jsonPath("$[1].code").value("BF50"));

        verify(promotionService).getAllPromotions();
    }

    @Test
    @WithMockUser
    @DisplayName("Should get promotion by ID")
    void shouldGetPromotionById() throws Exception {
        // Given
        when(promotionService.getPromotionByIdResponse(1L)).thenReturn(promotionResponse);

        // When & Then
        mockMvc.perform(get("/promotions/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Summer Sale"))
            .andExpect(jsonPath("$.discountValue").value(20.00))
            .andExpect(jsonPath("$.code").value("SUMMER20"))
            .andExpect(jsonPath("$.stateId").value(1L))
            .andExpect(jsonPath("$.stateName").value("ACTIVE"));

        verify(promotionService).getPromotionByIdResponse(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when promotion not found by ID")
    void shouldReturn404WhenPromotionNotFoundById() throws Exception {
        // Given
        when(promotionService.getPromotionByIdResponse(99L))
            .thenThrow(new EntityNotFoundException("Promotion with ID 99 not found"));

        // When & Then
        mockMvc.perform(get("/promotions/99"))
            .andExpect(status().isNotFound());

        verify(promotionService).getPromotionByIdResponse(99L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should get promotion by code")
    void shouldGetPromotionByCode() throws Exception {
        // Given
        String code = "SUMMER20";
        when(promotionService.getPromotionByCode(code)).thenReturn(promotionResponse);

        // When & Then
        mockMvc.perform(get("/promotions/code/{code}", code))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Summer Sale"))
            .andExpect(jsonPath("$.code").value("SUMMER20"));

        verify(promotionService).getPromotionByCode(code);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when promotion not found by code")
    void shouldReturn404WhenPromotionNotFoundByCode() throws Exception {
        // Given
        String code = "NONEXISTENT";
        when(promotionService.getPromotionByCode(code))
            .thenThrow(new EntityNotFoundException("Promotion with code " + code + " not found"));

        // When & Then
        mockMvc.perform(get("/promotions/code/{code}", code))
            .andExpect(status().isNotFound());

        verify(promotionService).getPromotionByCode(code);
    }

    @Test
    @WithMockUser
    @DisplayName("Should create promotion successfully")
    void shouldCreatePromotionSuccessfully() throws Exception {
        // Given
        when(promotionService.createPromotion(any(PromotionDto.class)))
            .thenReturn(promotionResponse);

        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(promotionDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Summer Sale"))
            .andExpect(jsonPath("$.discountValue").value(20.00))
            .andExpect(jsonPath("$.code").value("SUMMER20"));

        verify(promotionService).createPromotion(any(PromotionDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when creating promotion with invalid data")
    void shouldReturn400WhenCreatingPromotionWithInvalidData() throws Exception {
        // Given
        PromotionDto invalidDto = new PromotionDto("", new BigDecimal("10.00"), "CODE", 1L);

        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(promotionService, never()).createPromotion(any(PromotionDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when creating promotion with null name")
    void shouldReturn400WhenCreatingPromotionWithNullName() throws Exception {
        // Given
        PromotionDto invalidDto = new PromotionDto(null, new BigDecimal("10.00"), "CODE", 1L);

        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(promotionService, never()).createPromotion(any(PromotionDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when creating promotion with null discount value")
    void shouldReturn400WhenCreatingPromotionWithNullDiscountValue() throws Exception {
        // Given
        PromotionDto invalidDto = new PromotionDto("Test Promotion", null, "CODE", 1L);

        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(promotionService, never()).createPromotion(any(PromotionDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when creating promotion with negative discount value")
    void shouldReturn400WhenCreatingPromotionWithNegativeDiscountValue() throws Exception {
        // Given
        PromotionDto invalidDto = new PromotionDto("Test Promotion", new BigDecimal("-10.00"), "CODE", 1L);

        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(promotionService, never()).createPromotion(any(PromotionDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when creating promotion with null code")
    void shouldReturn400WhenCreatingPromotionWithNullCode() throws Exception {
        // Given
        PromotionDto invalidDto = new PromotionDto("Test Promotion", new BigDecimal("10.00"), null, 1L);

        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(promotionService, never()).createPromotion(any(PromotionDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when creating promotion with null state ID")
    void shouldReturn400WhenCreatingPromotionWithNullStateId() throws Exception {
        // Given
        PromotionDto invalidDto = new PromotionDto("Test Promotion", new BigDecimal("10.00"), "CODE", null);

        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(promotionService, never()).createPromotion(any(PromotionDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should update promotion successfully")
    void shouldUpdatePromotionSuccessfully() throws Exception {
        // Given
        PromotionResponse updatedResponse = new PromotionResponse(
            1L, "Updated Summer Sale", new BigDecimal("25.00"), "UPDATED25", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(promotionService.updatePromotion(eq(1L), any(PromotionDto.class)))
            .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/promotions/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(promotionDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Updated Summer Sale"))
            .andExpect(jsonPath("$.discountValue").value(25.00))
            .andExpect(jsonPath("$.code").value("UPDATED25"));

        verify(promotionService).updatePromotion(eq(1L), any(PromotionDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when updating non-existent promotion")
    void shouldReturn404WhenUpdatingNonExistentPromotion() throws Exception {
        // Given
        when(promotionService.updatePromotion(eq(99L), any(PromotionDto.class)))
            .thenThrow(new EntityNotFoundException("Promotion with ID 99 not found"));

        // When & Then
        mockMvc.perform(put("/promotions/99")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(promotionDto)))
            .andExpect(status().isNotFound());

        verify(promotionService).updatePromotion(eq(99L), any(PromotionDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when updating with invalid data")
    void shouldReturn400WhenUpdatingWithInvalidData() throws Exception {
        // Given
        PromotionDto invalidDto = new PromotionDto("", new BigDecimal("10.00"), "CODE", 1L);

        // When & Then
        mockMvc.perform(put("/promotions/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpectedstatus().isBadRequest());

        verify(promotionService, never()).updatePromotion(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should delete promotion successfully")
    void shouldDeletePromotionSuccessfully() throws Exception {
        // Given
        doNothing().when(promotionService).deletePromotion(1L);

        // When & Then
        mockMvc.perform(delete("/promotions/1")
                .with(csrf()))
            .andExpect(status().isNoContent());

        verify(promotionService).deletePromotion(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when deleting non-existent promotion")
    void shouldReturn404WhenDeletingNonExistentPromotion() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Promotion with ID 99 not found"))
            .when(promotionService).deletePromotion(99L);

        // When & Then
        mockMvc.perform(delete("/promotions/99")
                .with(csrf()))
            .andExpect(status().isNotFound());

        verify(promotionService).deletePromotion(99L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should get promotions by state")
    void shouldGetPromotionsByState() throws Exception {
        // Given
        List<PromotionResponse> responses = Arrays.asList(promotionResponse);
        when(promotionService.getPromotionsByState(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/promotions/state/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("Summer Sale"))
            .andExpect(jsonPath("$[0].stateId").value(1L));

        verify(promotionService).getPromotionsByState(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return empty list when no promotions found for state")
    void shouldReturnEmptyListWhenNoPromotionsFoundForState() throws Exception {
        // Given
        when(promotionService.getPromotionsByState(99L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/promotions/state/99"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));

        verify(promotionService).getPromotionsByState(99L);
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/promotions"))
            .andExpectedstatus().isUnauthorized());

        verify(promotionService, never()).getAllPromotions();
    }

    @Test
    @DisplayName("Should return 403 when CSRF token missing on POST")
    void shouldReturn403WhenCsrfTokenMissingOnPost() throws Exception {
        // When & Then
        mockMvc.perform(post("/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(promotionDto)))
            .andExpect(status().isForbidden());

        verify(promotionService, never()).createPromotion(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle malformed JSON")
    void shouldHandleMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid-json"))
            .andExpect(status().isBadRequest());

        verify(promotionService, never()).createPromotion(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle missing request body")
    void shouldHandleMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(promotionService, never()).createPromotion(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle invalid path parameter for ID")
    void shouldHandleInvalidPathParameterForId() throws Exception {
        // When & Then
        mockMvc.perform(get("/promotions/invalid"))
            .andExpect(status().isBadRequest());

        verify(promotionService, never()).getPromotionByIdResponse(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle special characters in code path parameter")
    void shouldHandleSpecialCharactersInCodePathParameter() throws Exception {
        // Given
        String codeWithSpecialChars = "SPECIAL@2024#";
        when(promotionService.getPromotionByCode(codeWithSpecialChars)).thenReturn(promotionResponse);

        // When & Then
        mockMvc.perform(get("/promotions/code/{code}", codeWithSpecialChars))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("SUMMER20")); // Response has different code

        verify(promotionService).getPromotionByCode(codeWithSpecialChars);
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle empty code path parameter")
    void shouldHandleEmptyCodePathParameter() throws Exception {
        // When & Then - This should result in a different endpoint being matched or 404
        mockMvc.perform(get("/promotions/code/"))
            .andExpect(status().isNotFound()); // Spring will return 404 for empty path variable

        verify(promotionService, never()).getPromotionByCode(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should validate discount value precision")
    void shouldValidateDiscountValuePrecision() throws Exception {
        // Given
        PromotionDto precisionDto = new PromotionDto("Precision Test", new BigDecimal("12.345"), "PRECISION", 1L);
        
        when(promotionService.createPromotion(any(PromotionDto.class)))
            .thenReturn(promotionResponse);

        // When & Then
        mockMvc.perform(post("/promotions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(precisionDto)))
            .andExpect(status().isCreated());

        verify(promotionService).createPromotion(any(PromotionDto.class));
    }
}