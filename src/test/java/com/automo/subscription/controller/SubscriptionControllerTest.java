package com.automo.subscription.controller;

import com.automo.subscription.dto.SubscriptionDto;
import com.automo.subscription.response.SubscriptionResponse;
import com.automo.subscription.service.SubscriptionService;
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
import java.time.LocalDate;
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

@WebMvcTest(SubscriptionController.class)
@DisplayName("Tests for SubscriptionController")
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all subscriptions successfully")
    void shouldGetAllSubscriptionsSuccessfully() throws Exception {
        // Given
        SubscriptionResponse response1 = createTestSubscriptionResponse(1L);
        SubscriptionResponse response2 = createTestSubscriptionResponse(2L);
        List<SubscriptionResponse> responses = Arrays.asList(response1, response2);

        when(subscriptionService.getAllSubscriptions()).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(subscriptionService).getAllSubscriptions();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get subscription by ID successfully")
    void shouldGetSubscriptionByIdSuccessfully() throws Exception {
        // Given
        SubscriptionResponse response = createTestSubscriptionResponse(1L);
        when(subscriptionService.getSubscriptionByIdResponse(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/subscriptions/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.planId", is(1)));

        verify(subscriptionService).getSubscriptionByIdResponse(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when subscription not found by ID")
    void shouldReturn404WhenSubscriptionNotFoundById() throws Exception {
        // Given
        when(subscriptionService.getSubscriptionByIdResponse(1L))
                .thenThrow(new EntityNotFoundException("Subscription not found"));

        // When & Then
        mockMvc.perform(get("/subscriptions/1"))
                .andExpect(status().isNotFound());

        verify(subscriptionService).getSubscriptionByIdResponse(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create subscription successfully")
    void shouldCreateSubscriptionSuccessfully() throws Exception {
        // Given
        SubscriptionDto subscriptionDto = TestDataFactory.createValidSubscriptionDto(1L, 1L, 1L);
        SubscriptionResponse response = createTestSubscriptionResponse(1L);

        when(subscriptionService.createSubscription(any(SubscriptionDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/subscriptions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(subscriptionService).createSubscription(any(SubscriptionDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid subscription data")
    void shouldReturn400ForInvalidSubscriptionData() throws Exception {
        // Given - Invalid DTO with null required fields
        SubscriptionDto invalidDto = new SubscriptionDto(
                null, // userId is required
                null, // planId is required
                null,
                null, // price is required
                null, // startDate is required
                null, // endDate is required
                null, // messageCount is required
                null  // stateId is required
        );

        // When & Then
        mockMvc.perform(post("/subscriptions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(subscriptionService, never()).createSubscription(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for negative price")
    void shouldReturn400ForNegativePrice() throws Exception {
        // Given
        SubscriptionDto invalidDto = new SubscriptionDto(
                1L, 1L, null, 
                new BigDecimal("-10.00"), // negative price
                LocalDate.now(), LocalDate.now().plusMonths(1),
                1000, 1L
        );

        // When & Then
        mockMvc.perform(post("/subscriptions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(subscriptionService, never()).createSubscription(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for negative message count")
    void shouldReturn400ForNegativeMessageCount() throws Exception {
        // Given
        SubscriptionDto invalidDto = new SubscriptionDto(
                1L, 1L, null,
                new BigDecimal("29.99"), LocalDate.now(), LocalDate.now().plusMonths(1),
                -100, // negative message count
                1L
        );

        // When & Then
        mockMvc.perform(post("/subscriptions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(subscriptionService, never()).createSubscription(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update subscription successfully")
    void shouldUpdateSubscriptionSuccessfully() throws Exception {
        // Given
        SubscriptionDto subscriptionDto = TestDataFactory.createValidSubscriptionDto(1L, 1L, 1L);
        SubscriptionResponse response = createTestSubscriptionResponse(1L);

        when(subscriptionService.updateSubscription(eq(1L), any(SubscriptionDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/subscriptions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));

        verify(subscriptionService).updateSubscription(eq(1L), any(SubscriptionDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when updating non-existent subscription")
    void shouldReturn404WhenUpdatingNonExistentSubscription() throws Exception {
        // Given
        SubscriptionDto subscriptionDto = TestDataFactory.createValidSubscriptionDto(1L, 1L, 1L);
        when(subscriptionService.updateSubscription(eq(1L), any(SubscriptionDto.class)))
                .thenThrow(new EntityNotFoundException("Subscription not found"));

        // When & Then
        mockMvc.perform(put("/subscriptions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionDto)))
                .andExpect(status().isNotFound());

        verify(subscriptionService).updateSubscription(eq(1L), any(SubscriptionDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete subscription successfully")
    void shouldDeleteSubscriptionSuccessfully() throws Exception {
        // Given
        doNothing().when(subscriptionService).deleteSubscription(1L);

        // When & Then
        mockMvc.perform(delete("/subscriptions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(subscriptionService).deleteSubscription(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when deleting non-existent subscription")
    void shouldReturn404WhenDeletingNonExistentSubscription() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Subscription not found"))
                .when(subscriptionService).deleteSubscription(1L);

        // When & Then
        mockMvc.perform(delete("/subscriptions/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(subscriptionService).deleteSubscription(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get subscriptions by state")
    void shouldGetSubscriptionsByState() throws Exception {
        // Given
        List<SubscriptionResponse> responses = Arrays.asList(createTestSubscriptionResponse(1L));
        when(subscriptionService.getSubscriptionsByState(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/subscriptions/state/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(subscriptionService).getSubscriptionsByState(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get subscriptions by user")
    void shouldGetSubscriptionsByUser() throws Exception {
        // Given
        List<SubscriptionResponse> responses = Arrays.asList(createTestSubscriptionResponse(1L));
        when(subscriptionService.getSubscriptionsByUser(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/subscriptions/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(1)));

        verify(subscriptionService).getSubscriptionsByUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get subscriptions by plan")
    void shouldGetSubscriptionsByPlan() throws Exception {
        // Given
        List<SubscriptionResponse> responses = Arrays.asList(createTestSubscriptionResponse(1L));
        when(subscriptionService.getSubscriptionsByPlan(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/subscriptions/plan/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].planId", is(1)));

        verify(subscriptionService).getSubscriptionsByPlan(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get subscriptions by promotion")
    void shouldGetSubscriptionsByPromotion() throws Exception {
        // Given
        List<SubscriptionResponse> responses = Arrays.asList(createTestSubscriptionResponse(1L));
        when(subscriptionService.getSubscriptionsByPromotion(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/subscriptions/promotion/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(subscriptionService).getSubscriptionsByPromotion(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get subscriptions by date range")
    void shouldGetSubscriptionsByDateRange() throws Exception {
        // Given
        List<SubscriptionResponse> responses = Arrays.asList(createTestSubscriptionResponse(1L));
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        
        when(subscriptionService.getSubscriptionsByDateRange(startDate, endDate)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/subscriptions/date-range")
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(subscriptionService).getSubscriptionsByDateRange(startDate, endDate);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid date format in date range")
    void shouldReturn400ForInvalidDateFormatInDateRange() throws Exception {
        // When & Then
        mockMvc.perform(get("/subscriptions/date-range")
                        .param("startDate", "invalid-date")
                        .param("endDate", "2023-12-31"))
                .andExpect(status().isBadRequest());

        verify(subscriptionService, never()).getSubscriptionsByDateRange(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get expired subscriptions")
    void shouldGetExpiredSubscriptions() throws Exception {
        // Given
        List<SubscriptionResponse> responses = Arrays.asList(createTestSubscriptionResponse(1L));
        LocalDate date = LocalDate.of(2023, 6, 1);
        
        when(subscriptionService.getExpiredSubscriptions(date)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/subscriptions/expired")
                        .param("date", "2023-06-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(subscriptionService).getExpiredSubscriptions(date);
    }

    @Test
    @DisplayName("Should require authentication for all endpoints")
    void shouldRequireAuthenticationForAllEndpoints() throws Exception {
        // Test GET all
        mockMvc.perform(get("/subscriptions"))
                .andExpect(status().isUnauthorized());

        // Test GET by ID
        mockMvc.perform(get("/subscriptions/1"))
                .andExpect(status().isUnauthorized());

        // Test POST
        mockMvc.perform(post("/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        // Test PUT
        mockMvc.perform(put("/subscriptions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        // Test DELETE
        mockMvc.perform(delete("/subscriptions/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access for users without proper role")
    void shouldDenyAccessForUsersWithoutProperRole() throws Exception {
        // When & Then
        mockMvc.perform(post("/subscriptions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    private SubscriptionResponse createTestSubscriptionResponse(Long id) {
        return new SubscriptionResponse(
                id,
                1L, // userId
                "Test User",
                1L, // planId
                "Basic Plan",
                null, // promotionId
                null, // promotionName
                new BigDecimal("29.99"),
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                1000,
                1L, // stateId
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}