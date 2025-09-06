package com.automo.agent.controller;

import com.automo.agent.dto.AgentDto;
import com.automo.agent.response.AgentResponse;
import com.automo.agent.service.AgentService;
import com.automo.config.security.JwtUtils;
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

@WebMvcTest(AgentController.class)
@ActiveProfiles("test")
@DisplayName("Tests for AgentController")
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentService agentService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private AgentResponse testAgentResponse;
    private AgentDto testAgentDto;
    private List<AgentResponse> agentListResponse;

    @BeforeEach
    void setUp() {
        testAgentResponse = new AgentResponse(
            1L,
            "Test Agent",
            "Test agent description",
            "Lisboa, Portugal",
            "No restrictions",
            "Standard flow",
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        testAgentDto = new AgentDto(
            "Test Agent",
            "Test agent description",
            "Lisboa, Portugal",
            "No restrictions",
            "Standard flow",
            1L
        );

        agentListResponse = Arrays.asList(
            testAgentResponse,
            new AgentResponse(2L, "Agent 2", "Description 2", "Porto", "None", "Flow 2", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("Should get all agents successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllAgentsSuccessfully() throws Exception {
        // Given
        when(agentService.getAllAgents()).thenReturn(agentListResponse);

        // When & Then
        mockMvc.perform(get("/agents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Agent"))
                .andExpect(jsonPath("$[0].description").value("Test agent description"))
                .andExpect(jsonPath("$[0].location").value("Lisboa, Portugal"))
                .andExpect(jsonPath("$[0].restrictions").value("No restrictions"))
                .andExpect(jsonPath("$[0].activityFlow").value("Standard flow"))
                .andExpect(jsonPath("$[0].stateId").value(1L))
                .andExpect(jsonPath("$[0].stateName").value("ACTIVE"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Agent 2"));

        verify(agentService).getAllAgents();
    }

    @Test
    @DisplayName("Should return empty list when no agents exist")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyListWhenNoAgentsExist() throws Exception {
        // Given
        when(agentService.getAllAgents()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/agents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(agentService).getAllAgents();
    }

    @Test
    @DisplayName("Should get agent by id successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAgentByIdSuccessfully() throws Exception {
        // Given
        Long agentId = 1L;
        when(agentService.getAgentByIdResponse(agentId)).thenReturn(testAgentResponse);

        // When & Then
        mockMvc.perform(get("/agents/{id}", agentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Agent"))
                .andExpect(jsonPath("$.description").value("Test agent description"))
                .andExpect(jsonPath("$.location").value("Lisboa, Portugal"))
                .andExpect(jsonPath("$.restrictions").value("No restrictions"))
                .andExpect(jsonPath("$.activityFlow").value("Standard flow"))
                .andExpect(jsonPath("$.stateId").value(1L))
                .andExpect(jsonPath("$.stateName").value("ACTIVE"));

        verify(agentService).getAgentByIdResponse(agentId);
    }

    @Test
    @DisplayName("Should return 404 when agent not found by id")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenAgentNotFoundById() throws Exception {
        // Given
        Long agentId = 999L;
        when(agentService.getAgentByIdResponse(agentId))
                .thenThrow(new EntityNotFoundException("Agent with ID " + agentId + " not found"));

        // When & Then
        mockMvc.perform(get("/agents/{id}", agentId))
                .andExpect(status().isNotFound());

        verify(agentService).getAgentByIdResponse(agentId);
    }

    @Test
    @DisplayName("Should create agent successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateAgentSuccessfully() throws Exception {
        // Given
        when(agentService.createAgent(any(AgentDto.class))).thenReturn(testAgentResponse);

        // When & Then
        mockMvc.perform(post("/agents")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAgentDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Agent"))
                .andExpect(jsonPath("$.description").value("Test agent description"))
                .andExpect(jsonPath("$.stateId").value(1L))
                .andExpect(jsonPath("$.stateName").value("ACTIVE"));

        verify(agentService).createAgent(any(AgentDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating agent with invalid data")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenCreatingAgentWithInvalidData() throws Exception {
        // Given - AgentDto with invalid data (null name and stateId)
        AgentDto invalidDto = new AgentDto(null, "Description", "Location", "Restrictions", "Flow", null);

        // When & Then
        mockMvc.perform(post("/agents")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(agentService, never()).createAgent(any(AgentDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating agent with blank name")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenCreatingAgentWithBlankName() throws Exception {
        // Given - AgentDto with blank name
        AgentDto invalidDto = new AgentDto("", "Description", "Location", "Restrictions", "Flow", 1L);

        // When & Then
        mockMvc.perform(post("/agents")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(agentService, never()).createAgent(any(AgentDto.class));
    }

    @Test
    @DisplayName("Should update agent successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateAgentSuccessfully() throws Exception {
        // Given
        Long agentId = 1L;
        when(agentService.updateAgent(eq(agentId), any(AgentDto.class))).thenReturn(testAgentResponse);

        // When & Then
        mockMvc.perform(put("/agents/{id}", agentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAgentDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Agent"))
                .andExpect(jsonPath("$.description").value("Test agent description"));

        verify(agentService).updateAgent(eq(agentId), any(AgentDto.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existing agent")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenUpdatingNonExistingAgent() throws Exception {
        // Given
        Long agentId = 999L;
        when(agentService.updateAgent(eq(agentId), any(AgentDto.class)))
                .thenThrow(new EntityNotFoundException("Agent with ID " + agentId + " not found"));

        // When & Then
        mockMvc.perform(put("/agents/{id}", agentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAgentDto)))
                .andExpect(status().isNotFound());

        verify(agentService).updateAgent(eq(agentId), any(AgentDto.class));
    }

    @Test
    @DisplayName("Should return 400 when updating agent with invalid data")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenUpdatingAgentWithInvalidData() throws Exception {
        // Given
        Long agentId = 1L;
        AgentDto invalidDto = new AgentDto(null, "Description", "Location", "Restrictions", "Flow", null);

        // When & Then
        mockMvc.perform(put("/agents/{id}", agentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(agentService, never()).updateAgent(eq(agentId), any(AgentDto.class));
    }

    @Test
    @DisplayName("Should delete agent successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteAgentSuccessfully() throws Exception {
        // Given
        Long agentId = 1L;
        doNothing().when(agentService).deleteAgent(agentId);

        // When & Then
        mockMvc.perform(delete("/agents/{id}", agentId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(agentService).deleteAgent(agentId);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existing agent")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenDeletingNonExistingAgent() throws Exception {
        // Given
        Long agentId = 999L;
        doThrow(new EntityNotFoundException("Agent with ID " + agentId + " not found"))
                .when(agentService).deleteAgent(agentId);

        // When & Then
        mockMvc.perform(delete("/agents/{id}", agentId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(agentService).deleteAgent(agentId);
    }

    @Test
    @DisplayName("Should get agents by state successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAgentsByStateSuccessfully() throws Exception {
        // Given
        Long stateId = 1L;
        when(agentService.getAgentsByState(stateId)).thenReturn(agentListResponse);

        // When & Then
        mockMvc.perform(get("/agents/state/{stateId}", stateId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Agent"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Agent 2"));

        verify(agentService).getAgentsByState(stateId);
    }

    @Test
    @DisplayName("Should return empty list when no agents exist for state")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyListWhenNoAgentsExistForState() throws Exception {
        // Given
        Long stateId = 999L;
        when(agentService.getAgentsByState(stateId)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/agents/state/{stateId}", stateId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(agentService).getAgentsByState(stateId);
    }

    @Test
    @DisplayName("Should get agents by area successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldGetAgentsByAreaSuccessfully() throws Exception {
        // Given
        Long areaId = 1L;
        when(agentService.getAgentsByArea(areaId)).thenReturn(agentListResponse);

        // When & Then
        mockMvc.perform(get("/agents/area/{areaId}", areaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Agent"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Agent 2"));

        verify(agentService).getAgentsByArea(areaId);
    }

    @Test
    @DisplayName("Should return empty list when no agents exist for area")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyListWhenNoAgentsExistForArea() throws Exception {
        // Given
        Long areaId = 999L;
        when(agentService.getAgentsByArea(areaId)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/agents/area/{areaId}", areaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(agentService).getAgentsByArea(areaId);
    }

    @Test
    @DisplayName("Should search agents by name successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldSearchAgentsByNameSuccessfully() throws Exception {
        // Given
        String searchName = "Test";
        when(agentService.searchAgentsByName(searchName)).thenReturn(Collections.singletonList(testAgentResponse));

        // When & Then
        mockMvc.perform(get("/agents/search")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Agent"))
                .andExpect(jsonPath("$[0].description").value("Test agent description"));

        verify(agentService).searchAgentsByName(searchName);
    }

    @Test
    @DisplayName("Should return empty list when no agents match search criteria")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEmptyListWhenNoAgentsMatchSearchCriteria() throws Exception {
        // Given
        String searchName = "NonExistent";
        when(agentService.searchAgentsByName(searchName)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/agents/search")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(agentService).searchAgentsByName(searchName);
    }

    @Test
    @DisplayName("Should require authentication for all endpoints")
    void shouldRequireAuthenticationForAllEndpoints() throws Exception {
        // Test GET /agents
        mockMvc.perform(get("/agents"))
                .andExpect(status().isUnauthorized());

        // Test GET /agents/{id}
        mockMvc.perform(get("/agents/1"))
                .andExpect(status().isUnauthorized());

        // Test POST /agents
        mockMvc.perform(post("/agents")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAgentDto)))
                .andExpect(status().isUnauthorized());

        // Test PUT /agents/{id}
        mockMvc.perform(put("/agents/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAgentDto)))
                .andExpect(status().isUnauthorized());

        // Test DELETE /agents/{id}
        mockMvc.perform(delete("/agents/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        // Test GET /agents/state/{stateId}
        mockMvc.perform(get("/agents/state/1"))
                .andExpect(status().isUnauthorized());

        // Test GET /agents/area/{areaId}
        mockMvc.perform(get("/agents/area/1"))
                .andExpect(status().isUnauthorized());

        // Test GET /agents/search
        mockMvc.perform(get("/agents/search").param("name", "test"))
                .andExpect(status().isUnauthorized());

        verify(agentService, never()).getAllAgents();
        verify(agentService, never()).getAgentByIdResponse(any());
        verify(agentService, never()).createAgent(any());
        verify(agentService, never()).updateAgent(any(), any());
        verify(agentService, never()).deleteAgent(any());
        verify(agentService, never()).getAgentsByState(any());
        verify(agentService, never()).getAgentsByArea(any());
        verify(agentService, never()).searchAgentsByName(any());
    }

    @Test
    @DisplayName("Should handle malformed JSON in POST request")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleMalformedJsonInPostRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/agents")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ malformed json }"))
                .andExpect(status().isBadRequest());

        verify(agentService, never()).createAgent(any(AgentDto.class));
    }

    @Test
    @DisplayName("Should handle malformed JSON in PUT request")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleMalformedJsonInPutRequest() throws Exception {
        // When & Then
        mockMvc.perform(put("/agents/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ malformed json }"))
                .andExpect(status().isBadRequest());

        verify(agentService, never()).updateAgent(any(), any(AgentDto.class));
    }

    @Test
    @DisplayName("Should handle missing request body in POST")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleMissingRequestBodyInPost() throws Exception {
        // When & Then
        mockMvc.perform(post("/agents")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(agentService, never()).createAgent(any(AgentDto.class));
    }

    @Test
    @DisplayName("Should handle missing request body in PUT")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleMissingRequestBodyInPut() throws Exception {
        // When & Then
        mockMvc.perform(put("/agents/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(agentService, never()).updateAgent(any(), any(AgentDto.class));
    }
}