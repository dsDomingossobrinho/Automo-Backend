package com.automo.subscriptionPlan.controller;

import com.automo.subscriptionPlan.dto.SubscriptionPlanDto;
import com.automo.subscriptionPlan.response.SubscriptionPlanResponse;
import com.automo.subscriptionPlan.service.SubscriptionPlanService;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionPlanController.class)
@DisplayName("Tests for SubscriptionPlanController")
class SubscriptionPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionPlanService subscriptionPlanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all subscription plans successfully")
    void shouldGetAllSubscriptionPlansSuccessfully() throws Exception {
        // Given
        SubscriptionPlanResponse response1 = createTestSubscriptionPlanResponse(1L, "Basic Plan");
        SubscriptionPlanResponse response2 = createTestSubscriptionPlanResponse(2L, "Premium Plan");
        List<SubscriptionPlanResponse> responses = Arrays.asList(response1, response2);

        when(subscriptionPlanService.getAllSubscriptionPlans()).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/subscription-plans"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Basic Plan")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Premium Plan")));

        verify(subscriptionPlanService).getAllSubscriptionPlans();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get subscription plan by ID successfully")
    void shouldGetSubscriptionPlanByIdSuccessfully() throws Exception {
        // Given
        SubscriptionPlanResponse response = createTestSubscriptionPlanResponse(1L, "Basic Plan");
        when(subscriptionPlanService.getSubscriptionPlanByIdResponse(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/subscription-plans/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Basic Plan")))
                .andExpect(jsonPath("$.price", is(29.99)));

        verify(subscriptionPlanService).getSubscriptionPlanByIdResponse(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when subscription plan not found by ID")
    void shouldReturn404WhenSubscriptionPlanNotFoundById() throws Exception {
        // Given
        when(subscriptionPlanService.getSubscriptionPlanByIdResponse(1L))
                .thenThrow(new EntityNotFoundException("Subscription plan not found"));

        // When & Then
        mockMvc.perform(get("/subscription-plans/1"))
                .andExpect(status().isNotFound());

        verify(subscriptionPlanService).getSubscriptionPlanByIdResponse(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create subscription plan successfully")
    void shouldCreateSubscriptionPlanSuccessfully() throws Exception {
        // Given
        SubscriptionPlanDto planDto = TestDataFactory.createValidSubscriptionPlanDto(1L);
        SubscriptionPlanResponse response = createTestSubscriptionPlanResponse(1L, "Basic Plan");

        when(subscriptionPlanService.createSubscriptionPlan(any(SubscriptionPlanDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Basic Plan")))
                .andExpect(jsonPath("$.price", is(29.99)));

        verify(subscriptionPlanService).createSubscriptionPlan(any(SubscriptionPlanDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid subscription plan data")
    void shouldReturn400ForInvalidSubscriptionPlanData() throws Exception {
        // Given - Invalid DTO with null required fields
        SubscriptionPlanDto invalidDto = new SubscriptionPlanDto(
                null, // name is required
                null, // price is required
                "Description",
                null  // stateId is required
        );

        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(subscriptionPlanService, never()).createSubscriptionPlan(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for blank name")
    void shouldReturn400ForBlankName() throws Exception {
        // Given
        SubscriptionPlanDto invalidDto = new SubscriptionPlanDto(
                "", // blank name
                new BigDecimal("29.99"),
                "Description",
                1L
        );

        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(subscriptionPlanService, never()).createSubscriptionPlan(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for negative price")
    void shouldReturn400ForNegativePrice() throws Exception {
        // Given
        SubscriptionPlanDto invalidDto = new SubscriptionPlanDto(
                "Test Plan",
                new BigDecimal("-10.00"), // negative price
                "Description",
                1L
        );

        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(subscriptionPlanService, never()).createSubscriptionPlan(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for zero price")
    void shouldReturn400ForZeroPrice() throws Exception {
        // Given
        SubscriptionPlanDto invalidDto = new SubscriptionPlanDto(
                "Test Plan",
                BigDecimal.ZERO, // zero price
                "Description",
                1L
        );

        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(subscriptionPlanService, never()).createSubscriptionPlan(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create subscription plan with null description")
    void shouldCreateSubscriptionPlanWithNullDescription() throws Exception {
        // Given
        SubscriptionPlanDto planDto = new SubscriptionPlanDto(
                "Basic Plan",
                new BigDecimal("29.99"),
                null, // null description is allowed
                1L
        );
        SubscriptionPlanResponse response = createTestSubscriptionPlanResponse(1L, "Basic Plan");

        when(subscriptionPlanService.createSubscriptionPlan(any(SubscriptionPlanDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Basic Plan")));

        verify(subscriptionPlanService).createSubscriptionPlan(any(SubscriptionPlanDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update subscription plan successfully")
    void shouldUpdateSubscriptionPlanSuccessfully() throws Exception {
        // Given
        SubscriptionPlanDto planDto = TestDataFactory.createValidSubscriptionPlanDto(1L);
        SubscriptionPlanResponse response = createTestSubscriptionPlanResponse(1L, "Updated Plan");

        when(subscriptionPlanService.updateSubscriptionPlan(eq(1L), any(SubscriptionPlanDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/subscription-plans/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Plan")));

        verify(subscriptionPlanService).updateSubscriptionPlan(eq(1L), any(SubscriptionPlanDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when updating non-existent subscription plan")
    void shouldReturn404WhenUpdatingNonExistentSubscriptionPlan() throws Exception {
        // Given
        SubscriptionPlanDto planDto = TestDataFactory.createValidSubscriptionPlanDto(1L);
        when(subscriptionPlanService.updateSubscriptionPlan(eq(1L), any(SubscriptionPlanDto.class)))
                .thenThrow(new EntityNotFoundException("Subscription plan not found"));

        // When & Then
        mockMvc.perform(put("/subscription-plans/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDto)))
                .andExpect(status().isNotFound());

        verify(subscriptionPlanService).updateSubscriptionPlan(eq(1L), any(SubscriptionPlanDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when updating with invalid data")
    void shouldReturn400WhenUpdatingWithInvalidData() throws Exception {
        // Given - Invalid DTO with blank name
        SubscriptionPlanDto invalidDto = new SubscriptionPlanDto(
                "   ", // whitespace-only name
                new BigDecimal("29.99"),
                "Description",
                1L
        );

        // When & Then
        mockMvc.perform(put("/subscription-plans/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(subscriptionPlanService, never()).updateSubscriptionPlan(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete subscription plan successfully")
    void shouldDeleteSubscriptionPlanSuccessfully() throws Exception {
        // Given
        doNothing().when(subscriptionPlanService).deleteSubscriptionPlan(1L);

        // When & Then
        mockMvc.perform(delete("/subscription-plans/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(subscriptionPlanService).deleteSubscriptionPlan(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when deleting non-existent subscription plan")
    void shouldReturn404WhenDeletingNonExistentSubscriptionPlan() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Subscription plan not found"))
                .when(subscriptionPlanService).deleteSubscriptionPlan(1L);

        // When & Then
        mockMvc.perform(delete("/subscription-plans/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(subscriptionPlanService).deleteSubscriptionPlan(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get subscription plans by state")
    void shouldGetSubscriptionPlansByState() throws Exception {
        // Given
        SubscriptionPlanResponse response1 = createTestSubscriptionPlanResponse(1L, "Active Plan");
        SubscriptionPlanResponse response2 = createTestSubscriptionPlanResponse(2L, "Another Active Plan");
        List<SubscriptionPlanResponse> responses = Arrays.asList(response1, response2);

        when(subscriptionPlanService.getSubscriptionPlansByState(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/subscription-plans/state/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Active Plan")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Another Active Plan")));

        verify(subscriptionPlanService).getSubscriptionPlansByState(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when no plans found for state")
    void shouldReturnEmptyListWhenNoPlansFoundForState() throws Exception {
        // Given
        when(subscriptionPlanService.getSubscriptionPlansByState(999L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/subscription-plans/state/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(subscriptionPlanService).getSubscriptionPlansByState(999L);
    }

    @Test
    @DisplayName("Should require authentication for all endpoints")
    void shouldRequireAuthenticationForAllEndpoints() throws Exception {
        // Test GET all
        mockMvc.perform(get("/subscription-plans"))
                .andExpect(status().isUnauthorized());

        // Test GET by ID
        mockMvc.perform(get("/subscription-plans/1"))
                .andExpect(status().isUnauthorized());

        // Test POST
        mockMvc.perform(post("/subscription-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        // Test PUT
        mockMvc.perform(put("/subscription-plans/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        // Test DELETE
        mockMvc.perform(delete("/subscription-plans/1"))
                .andExpect(status().isUnauthorized());

        // Test GET by state
        mockMvc.perform(get("/subscription-plans/state/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access for users without proper role")
    void shouldDenyAccessForUsersWithoutProperRole() throws Exception {
        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle malformed JSON gracefully")
    void shouldHandleMalformedJsonGracefully() throws Exception {
        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                .andExpect(status().isBadRequest());

        verify(subscriptionPlanService, never()).createSubscriptionPlan(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle large price values")
    void shouldHandleLargePriceValues() throws Exception {
        // Given
        SubscriptionPlanDto planDto = new SubscriptionPlanDto(
                "Enterprise Plan",
                new BigDecimal("99999.99"),
                "Very expensive plan",
                1L
        );
        SubscriptionPlanResponse response = createTestSubscriptionPlanResponse(1L, "Enterprise Plan");

        when(subscriptionPlanService.createSubscriptionPlan(any(SubscriptionPlanDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));

        verify(subscriptionPlanService).createSubscriptionPlan(any(SubscriptionPlanDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle long description values")
    void shouldHandleLongDescriptionValues() throws Exception {
        // Given
        String longDescription = "This is a very long description ".repeat(50); // 1500+ characters
        SubscriptionPlanDto planDto = new SubscriptionPlanDto(
                "Plan with Long Description",
                new BigDecimal("29.99"),
                longDescription,
                1L
        );
        SubscriptionPlanResponse response = createTestSubscriptionPlanResponse(1L, "Plan with Long Description");

        when(subscriptionPlanService.createSubscriptionPlan(any(SubscriptionPlanDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));

        verify(subscriptionPlanService).createSubscriptionPlan(any(SubscriptionPlanDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exceptions gracefully")
    void shouldHandleServiceExceptionsGracefully() throws Exception {
        // Given
        SubscriptionPlanDto planDto = TestDataFactory.createValidSubscriptionPlanDto(1L);
        when(subscriptionPlanService.createSubscriptionPlan(any(SubscriptionPlanDto.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/subscription-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDto)))
                .andExpect(status().isInternalServerError());

        verify(subscriptionPlanService).createSubscriptionPlan(any(SubscriptionPlanDto.class));
    }

    private SubscriptionPlanResponse createTestSubscriptionPlanResponse(Long id, String name) {
        return new SubscriptionPlanResponse(
                id,
                name,
                new BigDecimal("29.99"),
                "Test subscription plan description",
                1L, // stateId
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}