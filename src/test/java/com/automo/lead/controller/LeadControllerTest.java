package com.automo.lead.controller;

import com.automo.config.security.JwtUtils;
import com.automo.lead.dto.LeadDto;
import com.automo.lead.response.LeadResponse;
import com.automo.lead.service.LeadService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeadController.class)
@ActiveProfiles("test")
@DisplayName("Tests for LeadController")
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadService leadService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private LeadDto validLeadDto;
    private LeadResponse leadResponse;
    private List<LeadResponse> leadResponseList;

    @BeforeEach
    void setUp() {
        validLeadDto = new LeadDto(
            1L, // identifierId
            "João Silva",
            "joao.silva@example.com",
            "912345678",
            "Lisboa",
            1L, // leadTypeId
            1L  // stateId
        );

        leadResponse = new LeadResponse(
            1L,
            1L,
            "João Silva",
            "joao.silva@example.com",
            "912345678",
            "Lisboa",
            1L,
            "CALL",
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        LeadResponse leadResponse2 = new LeadResponse(
            2L,
            2L,
            "Maria Santos",
            "maria.santos@example.com",
            "923456789",
            "Porto",
            1L,
            "CALL",
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        leadResponseList = Arrays.asList(leadResponse, leadResponse2);
    }

    @Test
    @DisplayName("Should get all leads successfully")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldGetAllLeadsSuccessfully() throws Exception {
        // Given
        when(leadService.getAllLeads()).thenReturn(leadResponseList);

        // When/Then
        mockMvc.perform(get("/leads")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("João Silva"))
                .andExpect(jsonPath("$[0].email").value("joao.silva@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Maria Santos"));

        verify(leadService).getAllLeads();
    }

    @Test
    @DisplayName("Should return empty list when no leads exist")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturnEmptyListWhenNoLeadsExist() throws Exception {
        // Given
        when(leadService.getAllLeads()).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/leads")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(leadService).getAllLeads();
    }

    @Test
    @DisplayName("Should get lead by ID successfully")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldGetLeadByIdSuccessfully() throws Exception {
        // Given
        when(leadService.getLeadByIdResponse(1L)).thenReturn(leadResponse);

        // When/Then
        mockMvc.perform(get("/leads/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao.silva@example.com"))
                .andExpect(jsonPath("$.contact").value("912345678"))
                .andExpect(jsonPath("$.zone").value("Lisboa"))
                .andExpect(jsonPath("$.leadTypeId").value(1))
                .andExpect(jsonPath("$.leadTypeName").value("CALL"))
                .andExpect(jsonPath("$.stateId").value(1))
                .andExpect(jsonPath("$.stateName").value("ACTIVE"));

        verify(leadService).getLeadByIdResponse(1L);
    }

    @Test
    @DisplayName("Should return 404 when lead not found by ID")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturn404WhenLeadNotFoundById() throws Exception {
        // Given
        when(leadService.getLeadByIdResponse(999L))
            .thenThrow(new EntityNotFoundException("Lead with ID 999 not found"));

        // When/Then
        mockMvc.perform(get("/leads/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(leadService).getLeadByIdResponse(999L);
    }

    @Test
    @DisplayName("Should create lead successfully")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldCreateLeadSuccessfully() throws Exception {
        // Given
        when(leadService.createLead(any(LeadDto.class))).thenReturn(leadResponse);

        // When/Then
        mockMvc.perform(post("/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLeadDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao.silva@example.com"))
                .andExpect(jsonPath("$.contact").value("912345678"))
                .andExpect(jsonPath("$.zone").value("Lisboa"));

        verify(leadService).createLead(any(LeadDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating lead with invalid data")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturn400WhenCreatingLeadWithInvalidData() throws Exception {
        // Given
        LeadDto invalidDto = new LeadDto(
            null, // invalid identifierId
            "", // invalid name
            "invalid-email", // invalid email
            "912345678",
            "Lisboa",
            1L,
            1L
        );

        // When/Then
        mockMvc.perform(post("/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(leadService, never()).createLead(any(LeadDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating lead with null name")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturn400WhenCreatingLeadWithNullName() throws Exception {
        // Given
        LeadDto invalidDto = new LeadDto(
            1L,
            null, // null name
            "test@example.com",
            "912345678",
            "Lisboa",
            1L,
            1L
        );

        // When/Then
        mockMvc.perform(post("/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(leadService, never()).createLead(any(LeadDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating lead with blank email")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturn400WhenCreatingLeadWithBlankEmail() throws Exception {
        // Given
        LeadDto invalidDto = new LeadDto(
            1L,
            "Test User",
            "", // blank email
            "912345678",
            "Lisboa",
            1L,
            1L
        );

        // When/Then
        mockMvc.perform(post("/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(leadService, never()).createLead(any(LeadDto.class));
    }

    @Test
    @DisplayName("Should update lead successfully")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldUpdateLeadSuccessfully() throws Exception {
        // Given
        LeadDto updateDto = new LeadDto(
            1L,
            "João Updated",
            "joao.updated@example.com",
            "999888777",
            "Porto",
            1L,
            1L
        );
        
        LeadResponse updatedResponse = new LeadResponse(
            1L,
            1L,
            "João Updated",
            "joao.updated@example.com",
            "999888777",
            "Porto",
            1L,
            "CALL",
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(leadService.updateLead(eq(1L), any(LeadDto.class))).thenReturn(updatedResponse);

        // When/Then
        mockMvc.perform(put("/leads/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Updated"))
                .andExpect(jsonPath("$.email").value("joao.updated@example.com"))
                .andExpect(jsonPath("$.contact").value("999888777"))
                .andExpect(jsonPath("$.zone").value("Porto"));

        verify(leadService).updateLead(eq(1L), any(LeadDto.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent lead")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturn404WhenUpdatingNonExistentLead() throws Exception {
        // Given
        when(leadService.updateLead(eq(999L), any(LeadDto.class)))
            .thenThrow(new EntityNotFoundException("Lead with ID 999 not found"));

        // When/Then
        mockMvc.perform(put("/leads/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLeadDto)))
                .andExpect(status().isNotFound());

        verify(leadService).updateLead(eq(999L), any(LeadDto.class));
    }

    @Test
    @DisplayName("Should delete lead successfully")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldDeleteLeadSuccessfully() throws Exception {
        // Given
        doNothing().when(leadService).deleteLead(1L);

        // When/Then
        mockMvc.perform(delete("/leads/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(leadService).deleteLead(1L);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent lead")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturn404WhenDeletingNonExistentLead() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Lead with ID 999 not found"))
            .when(leadService).deleteLead(999L);

        // When/Then
        mockMvc.perform(delete("/leads/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(leadService).deleteLead(999L);
    }

    @Test
    @DisplayName("Should get leads by state successfully")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldGetLeadsByStateSuccessfully() throws Exception {
        // Given
        List<LeadResponse> activeLeads = Arrays.asList(leadResponse);
        when(leadService.getLeadsByState(1L)).thenReturn(activeLeads);

        // When/Then
        mockMvc.perform(get("/leads/state/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].stateName").value("ACTIVE"));

        verify(leadService).getLeadsByState(1L);
    }

    @Test
    @DisplayName("Should get leads by lead type successfully")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldGetLeadsByLeadTypeSuccessfully() throws Exception {
        // Given
        List<LeadResponse> callLeads = Arrays.asList(leadResponse);
        when(leadService.getLeadsByLeadType(1L)).thenReturn(callLeads);

        // When/Then
        mockMvc.perform(get("/leads/type/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].leadTypeName").value("CALL"));

        verify(leadService).getLeadsByLeadType(1L);
    }

    @Test
    @DisplayName("Should get leads by identifier successfully")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldGetLeadsByIdentifierSuccessfully() throws Exception {
        // Given
        List<LeadResponse> identifierLeads = Arrays.asList(leadResponse);
        when(leadService.getLeadsByIdentifier(1L)).thenReturn(identifierLeads);

        // When/Then
        mockMvc.perform(get("/leads/identifier/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].identifierId").value(1));

        verify(leadService).getLeadsByIdentifier(1L);
    }

    @Test
    @DisplayName("Should return empty list when no leads found by state")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturnEmptyListWhenNoLeadsFoundByState() throws Exception {
        // Given
        when(leadService.getLeadsByState(999L)).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/leads/state/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(leadService).getLeadsByState(999L);
    }

    @Test
    @DisplayName("Should return empty list when no leads found by lead type")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturnEmptyListWhenNoLeadsFoundByLeadType() throws Exception {
        // Given
        when(leadService.getLeadsByLeadType(999L)).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/leads/type/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(leadService).getLeadsByLeadType(999L);
    }

    @Test
    @DisplayName("Should return empty list when no leads found by identifier")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturnEmptyListWhenNoLeadsFoundByIdentifier() throws Exception {
        // Given
        when(leadService.getLeadsByIdentifier(999L)).thenReturn(Collections.emptyList());

        // When/Then
        mockMvc.perform(get("/leads/identifier/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(leadService).getLeadsByIdentifier(999L);
    }

    @Test
    @DisplayName("Should return 401 when accessing leads without authentication")
    void shouldReturn401WhenAccessingLeadsWithoutAuthentication() throws Exception {
        // When/Then
        mockMvc.perform(get("/leads")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(leadService, never()).getAllLeads();
    }

    @Test
    @DisplayName("Should return 403 when creating lead without CSRF token")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldReturn403WhenCreatingLeadWithoutCsrfToken() throws Exception {
        // When/Then
        mockMvc.perform(post("/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLeadDto)))
                .andExpect(status().isForbidden());

        verify(leadService, never()).createLead(any(LeadDto.class));
    }

    @Test
    @DisplayName("Should handle large contact value")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldHandleLargeContactValue() throws Exception {
        // Given
        LeadDto dtoWithLongContact = new LeadDto(
            1L,
            "Test User",
            "test@example.com",
            "+351 21 123 4567 (extension: 9876)", // longer contact
            "Lisboa",
            1L,
            1L
        );

        LeadResponse responseWithLongContact = new LeadResponse(
            1L, 1L, "Test User", "test@example.com",
            "+351 21 123 4567 (extension: 9876)", "Lisboa",
            1L, "CALL", 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(leadService.createLead(any(LeadDto.class))).thenReturn(responseWithLongContact);

        // When/Then
        mockMvc.perform(post("/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithLongContact)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contact").value("+351 21 123 4567 (extension: 9876)"));

        verify(leadService).createLead(any(LeadDto.class));
    }

    @Test
    @DisplayName("Should handle special characters in name and zone")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldHandleSpecialCharactersInNameAndZone() throws Exception {
        // Given
        LeadDto dtoWithSpecialChars = new LeadDto(
            1L,
            "José María Gómez-Fernández",
            "jose.maria@example.com",
            "912345678",
            "São Paulo - Região Metropolitana",
            1L,
            1L
        );

        LeadResponse responseWithSpecialChars = new LeadResponse(
            1L, 1L, "José María Gómez-Fernández", "jose.maria@example.com",
            "912345678", "São Paulo - Região Metropolitana",
            1L, "CALL", 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(leadService.createLead(any(LeadDto.class))).thenReturn(responseWithSpecialChars);

        // When/Then
        mockMvc.perform(post("/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithSpecialChars)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("José María Gómez-Fernández"))
                .andExpect(jsonPath("$.zone").value("São Paulo - Região Metropolitana"));

        verify(leadService).createLead(any(LeadDto.class));
    }

    @Test
    @DisplayName("Should handle null optional fields")
    @WithMockUser(roles = {"USER", "ADMIN"})
    void shouldHandleNullOptionalFields() throws Exception {
        // Given
        LeadDto dtoWithNulls = new LeadDto(
            1L,
            "Test User",
            "test@example.com",
            null, // null contact
            null, // null zone
            1L,
            1L
        );

        LeadResponse responseWithNulls = new LeadResponse(
            1L, 1L, "Test User", "test@example.com",
            null, null,
            1L, "CALL", 1L, "ACTIVE",
            LocalDateTime.now(), LocalDateTime.now()
        );

        when(leadService.createLead(any(LeadDto.class))).thenReturn(responseWithNulls);

        // When/Then
        mockMvc.perform(post("/leads")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoWithNulls)))
                .andExpectStatus().isCreated())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.contact").doesNotExist())
                .andExpect(jsonPath("$.zone").doesNotExist());

        verify(leadService).createLead(any(LeadDto.class));
    }
}