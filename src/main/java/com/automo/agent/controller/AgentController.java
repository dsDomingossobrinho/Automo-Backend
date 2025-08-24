package com.automo.agent.controller;

import com.automo.agent.dto.AgentDto;
import com.automo.agent.response.AgentResponse;
import com.automo.agent.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
@Tag(name = "Agents", description = "Agent management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AgentController {

    private final AgentService agentService;

    @Operation(description = "List all agents", summary = "Get all agents")
    @ApiResponse(responseCode = "200", description = "Agents retrieved successfully")
    @GetMapping
    public ResponseEntity<List<AgentResponse>> getAllAgents() {
        return ResponseEntity.ok(agentService.getAllAgents());
    }

    @Operation(description = "Get agent by ID", summary = "Get a specific agent by ID")
    @ApiResponse(responseCode = "200", description = "Agent retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<AgentResponse> getAgentById(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.getAgentByIdResponse(id));
    }

    @Operation(description = "Create new agent", summary = "Create a new agent")
    @ApiResponse(responseCode = "201", description = "Agent created successfully")
    @PostMapping
    public ResponseEntity<AgentResponse> createAgent(@Valid @RequestBody AgentDto agentDto) {
        AgentResponse response = agentService.createAgent(agentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update agent", summary = "Update an existing agent")
    @ApiResponse(responseCode = "200", description = "Agent updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<AgentResponse> updateAgent(@PathVariable Long id, @Valid @RequestBody AgentDto agentDto) {
        return ResponseEntity.ok(agentService.updateAgent(id, agentDto));
    }

    @Operation(description = "Delete agent", summary = "Delete an agent")
    @ApiResponse(responseCode = "204", description = "Agent deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get agents by state", summary = "Get agents filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Agents retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<AgentResponse>> getAgentsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(agentService.getAgentsByState(stateId));
    }

    @Operation(description = "Get agents by area", summary = "Get agents filtered by area ID")
    @ApiResponse(responseCode = "200", description = "Agents retrieved successfully")
    @GetMapping("/area/{areaId}")
    public ResponseEntity<List<AgentResponse>> getAgentsByArea(@PathVariable Long areaId) {
        return ResponseEntity.ok(agentService.getAgentsByArea(areaId));
    }

    @Operation(description = "Search agents by name", summary = "Search agents by name (case insensitive)")
    @ApiResponse(responseCode = "200", description = "Agents retrieved successfully")
    @GetMapping("/search")
    public ResponseEntity<List<AgentResponse>> searchAgentsByName(@RequestParam String name) {
        return ResponseEntity.ok(agentService.searchAgentsByName(name));
    }
} 