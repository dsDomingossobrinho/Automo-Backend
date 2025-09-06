package com.automo.deal.controller;

import com.automo.config.security.JwtUtils;
import com.automo.deal.dto.DealDto;
import com.automo.deal.response.DealResponse;
import com.automo.deal.service.DealService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealController.class)
@ActiveProfiles("test")
@DisplayName("Tests for DealController")
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealService dealService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private DealDto validDealDto;
    private DealResponse dealResponse;

    @BeforeEach
    void setUp() {
        validDealDto = new DealDto(
            1L, // identifierId
            1L, // leadId
            1L, // promotionId
            new BigDecimal("50000.00"),
            LocalDate.now().plusDays(30),
            5,
            1L  // stateId
        );

        dealResponse = new DealResponse(
            1L,
            1L,
            1L,
            "Test Lead",
            1L,
            "Test Promotion",
            new BigDecimal("50000.00"),
            LocalDate.now().plusDays(30),
            5,
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all deals successfully")
    void shouldGetAllDealsSuccessfully() throws Exception {
        // Given
        DealResponse deal1 = new DealResponse(
            1L, 1L, 1L, "Lead 1", 1L, "Promotion 1", 
            new BigDecimal("50000.00"), LocalDate.now(), 5, 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );
        DealResponse deal2 = new DealResponse(
            2L, 1L, 2L, "Lead 2", 2L, "Promotion 2", 
            new BigDecimal("75000.00"), LocalDate.now(), 3, 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );
        List<DealResponse> deals = Arrays.asList(deal1, deal2);

        when(dealService.getAllDeals()).thenReturn(deals);

        // When & Then
        mockMvc.perform(get("/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].leadName").value("Lead 1"))
                .andExpect(jsonPath("$[0].total").value(50000.00))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].leadName").value("Lead 2"))
                .andExpect(jsonPath("$[1].total").value(75000.00));

        verify(dealService).getAllDeals();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get deal by id successfully")
    void shouldGetDealByIdSuccessfully() throws Exception {
        // Given
        Long dealId = 1L;
        when(dealService.getDealByIdResponse(dealId)).thenReturn(dealResponse);

        // When & Then
        mockMvc.perform(get("/deals/{id}", dealId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.leadName").value("Test Lead"))
                .andExpect(jsonPath("$.promotionName").value("Test Promotion"))
                .andExpect(jsonPath("$.total").value(50000.00))
                .andExpect(jsonPath("$.messageCount").value(5))
                .andExpect(jsonPath("$.stateName").value("ACTIVE"));

        verify(dealService).getDealByIdResponse(dealId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create deal successfully")
    void shouldCreateDealSuccessfully() throws Exception {
        // Given
        when(dealService.createDeal(any(DealDto.class))).thenReturn(dealResponse);

        // When & Then
        mockMvc.perform(post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDealDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.leadName").value("Test Lead"))
                .andExpect(jsonPath("$.total").value(50000.00))
                .andExpect(jsonPath("$.messageCount").value(5));

        verify(dealService).createDeal(any(DealDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create deal without optional fields")
    void shouldCreateDealWithoutOptionalFields() throws Exception {
        // Given
        DealDto minimalDto = new DealDto(
            1L,
            null, // no leadId
            null, // no promotionId
            new BigDecimal("30000.00"),
            null, // no deliveryDate
            3,
            1L
        );

        DealResponse minimalResponse = new DealResponse(
            1L, 1L, null, null, null, null,
            new BigDecimal("30000.00"), null, 3, 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(dealService.createDeal(any(DealDto.class))).thenReturn(minimalResponse);

        // When & Then
        mockMvc.perform(post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.leadId").doesNotExist())
                .andExpect(jsonPath("$.leadName").doesNotExist())
                .andExpect(jsonPath("$.promotionId").doesNotExist())
                .andExpect(jsonPath("$.promotionName").doesNotExist())
                .andExpect(jsonPath("$.total").value(30000.00))
                .andExpect(jsonPath("$.messageCount").value(3));

        verify(dealService).createDeal(any(DealDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update deal successfully")
    void shouldUpdateDealSuccessfully() throws Exception {
        // Given
        Long dealId = 1L;
        DealResponse updatedResponse = new DealResponse(
            dealId, 1L, 1L, "Updated Lead", 1L, "Updated Promotion",
            new BigDecimal("60000.00"), LocalDate.now().plusDays(45), 7, 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(dealService.updateDeal(eq(dealId), any(DealDto.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/deals/{id}", dealId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDealDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dealId))
                .andExpect(jsonPath("$.leadName").value("Updated Lead"))
                .andExpect(jsonPath("$.total").value(60000.00))
                .andExpect(jsonPath("$.messageCount").value(7));

        verify(dealService).updateDeal(eq(dealId), any(DealDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete deal successfully")
    void shouldDeleteDealSuccessfully() throws Exception {
        // Given
        Long dealId = 1L;
        doNothing().when(dealService).deleteDeal(dealId);

        // When & Then
        mockMvc.perform(delete("/deals/{id}", dealId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(dealService).deleteDeal(dealId);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get deals by state successfully")
    void shouldGetDealsByStateSuccessfully() throws Exception {
        // Given
        Long stateId = 1L;
        List<DealResponse> deals = Arrays.asList(dealResponse);

        when(dealService.getDealsByState(stateId)).thenReturn(deals);

        // When & Then
        mockMvc.perform(get("/deals/state/{stateId}", stateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].stateId").value(stateId));

        verify(dealService).getDealsByState(stateId);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get deals by identifier successfully")
    void shouldGetDealsByIdentifierSuccessfully() throws Exception {
        // Given
        Long identifierId = 1L;
        List<DealResponse> deals = Arrays.asList(dealResponse);

        when(dealService.getDealsByIdentifier(identifierId)).thenReturn(deals);

        // When & Then
        mockMvc.perform(get("/deals/identifier/{identifierId}", identifierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].identifierId").value(identifierId));

        verify(dealService).getDealsByIdentifier(identifierId);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get deals by lead successfully")
    void shouldGetDealsByLeadSuccessfully() throws Exception {
        // Given
        Long leadId = 1L;
        List<DealResponse> deals = Arrays.asList(dealResponse);

        when(dealService.getDealsByLead(leadId)).thenReturn(deals);

        // When & Then
        mockMvc.perform(get("/deals/lead/{leadId}", leadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].leadId").value(leadId));

        verify(dealService).getDealsByLead(leadId);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get deals by promotion successfully")
    void shouldGetDealsByPromotionSuccessfully() throws Exception {
        // Given
        Long promotionId = 1L;
        List<DealResponse> deals = Arrays.asList(dealResponse);

        when(dealService.getDealsByPromotion(promotionId)).thenReturn(deals);

        // When & Then
        mockMvc.perform(get("/deals/promotion/{promotionId}", promotionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].promotionId").value(promotionId));

        verify(dealService).getDealsByPromotion(promotionId);
    }

    @Test
    @DisplayName("Should return 401 for unauthorized access")
    void shouldReturn401ForUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/deals"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden create operation")
    void shouldReturn403ForForbiddenCreateOperation() throws Exception {
        mockMvc.perform(post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDealDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden update operation")
    void shouldReturn403ForForbiddenUpdateOperation() throws Exception {
        mockMvc.perform(put("/deals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDealDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden delete operation")
    void shouldReturn403ForForbiddenDeleteOperation() throws Exception {
        mockMvc.perform(delete("/deals/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid deal data - missing required fields")
    void shouldReturn400ForInvalidDealDataMissingRequiredFields() throws Exception {
        // Given - Deal with missing required fields
        DealDto invalidDto = new DealDto(
            null, // missing identifierId
            1L,
            1L,
            null, // missing total
            LocalDate.now(),
            null, // missing messageCount
            null  // missing stateId
        );

        // When & Then
        mockMvc.perform(post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).createDeal(any(DealDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid deal data - negative values")
    void shouldReturn400ForInvalidDealDataNegativeValues() throws Exception {
        // Given - Deal with negative values
        DealDto invalidDto = new DealDto(
            1L,
            1L,
            1L,
            new BigDecimal("-1000.00"), // negative total
            LocalDate.now(),
            -5, // negative messageCount
            1L
        );

        // When & Then
        mockMvc.perform(post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).createDeal(any(DealDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid deal data - zero values")
    void shouldReturn400ForInvalidDealDataZeroValues() throws Exception {
        // Given - Deal with zero values
        DealDto invalidDto = new DealDto(
            1L,
            1L,
            1L,
            BigDecimal.ZERO, // zero total
            LocalDate.now(),
            0, // zero messageCount
            1L
        );

        // When & Then
        mockMvc.perform(post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).createDeal(any(DealDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 when deal not found")
    void shouldReturn404WhenDealNotFound() throws Exception {
        // Given
        Long nonExistentDealId = 999L;
        when(dealService.getDealByIdResponse(nonExistentDealId))
            .thenThrow(new EntityNotFoundException("Deal with ID " + nonExistentDealId + " not found"));

        // When & Then
        mockMvc.perform(get("/deals/{id}", nonExistentDealId))
                .andExpect(status().isNotFound());

        verify(dealService).getDealByIdResponse(nonExistentDealId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when updating non-existent deal")
    void shouldReturn404WhenUpdatingNonExistentDeal() throws Exception {
        // Given
        Long nonExistentDealId = 999L;
        when(dealService.updateDeal(eq(nonExistentDealId), any(DealDto.class)))
            .thenThrow(new EntityNotFoundException("Deal with ID " + nonExistentDealId + " not found"));

        // When & Then
        mockMvc.perform(put("/deals/{id}", nonExistentDealId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDealDto))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(dealService).updateDeal(eq(nonExistentDealId), any(DealDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when deleting non-existent deal")
    void shouldReturn404WhenDeletingNonExistentDeal() throws Exception {
        // Given
        Long nonExistentDealId = 999L;
        doThrow(new EntityNotFoundException("Deal with ID " + nonExistentDealId + " not found"))
            .when(dealService).deleteDeal(nonExistentDealId);

        // When & Then
        mockMvc.perform(delete("/deals/{id}", nonExistentDealId)
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(dealService).deleteDeal(nonExistentDealId);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return empty list when no deals exist")
    void shouldReturnEmptyListWhenNoDealsExist() throws Exception {
        // Given
        when(dealService.getAllDeals()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(dealService).getAllDeals();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return empty list when no deals exist for state")
    void shouldReturnEmptyListWhenNoDealsExistForState() throws Exception {
        // Given
        Long stateId = 999L;
        when(dealService.getDealsByState(stateId)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/deals/state/{stateId}", stateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(dealService).getDealsByState(stateId);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle large total amounts in response")
    void shouldHandleLargeTotalAmountsInResponse() throws Exception {
        // Given
        DealResponse largeAmountResponse = new DealResponse(
            1L, 1L, 1L, "Test Lead", 1L, "Test Promotion",
            new BigDecimal("999999999.99"), LocalDate.now(), 5, 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(dealService.getAllDeals()).thenReturn(Arrays.asList(largeAmountResponse));

        // When & Then
        mockMvc.perform(get("/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].total").value(999999999.99));

        verify(dealService).getAllDeals();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle high message counts in response")
    void shouldHandleHighMessageCountsInResponse() throws Exception {
        // Given
        DealResponse highCountResponse = new DealResponse(
            1L, 1L, 1L, "Test Lead", 1L, "Test Promotion",
            new BigDecimal("50000.00"), LocalDate.now(), 999999, 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(dealService.getDealByIdResponse(1L)).thenReturn(highCountResponse);

        // When & Then
        mockMvc.perform(get("/deals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageCount").value(999999));

        verify(dealService).getDealByIdResponse(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle deals with null optional fields in response")
    void shouldHandleDealsWithNullOptionalFieldsInResponse() throws Exception {
        // Given
        DealResponse minimalResponse = new DealResponse(
            1L, 1L, null, null, null, null,
            new BigDecimal("30000.00"), null, 3, 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(dealService.getDealByIdResponse(1L)).thenReturn(minimalResponse);

        // When & Then
        mockMvc.perform(get("/deals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.leadId").doesNotExist())
                .andExpect(jsonPath("$.leadName").doesNotExist())
                .andExpect(jsonPath("$.promotionId").doesNotExist())
                .andExpect(jsonPath("$.promotionName").doesNotExist())
                .andExpect(jsonPath("$.deliveryDate").doesNotExist())
                .andExpect(jsonPath("$.total").value(30000.00));

        verify(dealService).getDealByIdResponse(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle malformed JSON")
    void shouldHandleMalformedJSON() throws Exception {
        // Given - malformed JSON
        String malformedJson = "{\"identifierId\":1,\"total\":\"invalid-number\"}";

        // When & Then
        mockMvc.perform(post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson)
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).createDeal(any(DealDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle empty request body")
    void shouldHandleEmptyRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).createDeal(any(DealDto.class));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("Should allow AGENT role for read operations")
    void shouldAllowAgentRoleForReadOperations() throws Exception {
        // Given
        when(dealService.getAllDeals()).thenReturn(Arrays.asList(dealResponse));

        // When & Then
        mockMvc.perform(get("/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(dealService).getAllDeals();
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("Should deny AGENT role for create operations")
    void shouldDenyAgentRoleForCreateOperations() throws Exception {
        // When & Then
        mockMvc.perform(post("/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDealDto))
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(dealService, never()).createDeal(any(DealDto.class));
    }
}