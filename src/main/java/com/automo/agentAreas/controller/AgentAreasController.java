package com.automo.agentAreas.controller;

import com.automo.agentAreas.dto.AgentAreasDto;
import com.automo.agentAreas.response.AgentAreasResponse;
import com.automo.agentAreas.service.AgentAreasService;
import com.automo.config.security.SecurityConfig;
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
@RequestMapping("/agent-areas")
@RequiredArgsConstructor
@Tag(name = "Agent Areas", description = "Agent Areas management endpoints")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class AgentAreasController {

    private final AgentAreasService agentAreasService;

    @PostMapping
    @Operation(summary = "Create a new agent area association", description = "Creates a new association between an agent and an area")
    @ApiResponse(responseCode = "201", description = "Agent area association created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<AgentAreasResponse> createAgentAreas(@Valid @RequestBody AgentAreasDto agentAreasDto) {
        AgentAreasResponse response = agentAreasService.createAgentAreas(agentAreasDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an agent area association", description = "Updates an existing association between an agent and an area")
    @ApiResponse(responseCode = "200", description = "Agent area association updated successfully")
    @ApiResponse(responseCode = "404", description = "Agent area association not found")
    public ResponseEntity<AgentAreasResponse> updateAgentAreas(@PathVariable Long id, @Valid @RequestBody AgentAreasDto agentAreasDto) {
        AgentAreasResponse response = agentAreasService.updateAgentAreas(id, agentAreasDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all agent area associations", description = "Retrieves all associations between agents and areas")
    @ApiResponse(responseCode = "200", description = "Agent area associations retrieved successfully")
    public ResponseEntity<List<AgentAreasResponse>> getAllAgentAreas() {
        List<AgentAreasResponse> response = agentAreasService.getAllAgentAreas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get agent area association by ID", description = "Retrieves a specific association between an agent and an area")
    @ApiResponse(responseCode = "200", description = "Agent area association retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Agent area association not found")
    public ResponseEntity<AgentAreasResponse> getAgentAreasById(@PathVariable Long id) {
        AgentAreasResponse response = agentAreasService.getAgentAreasByIdResponse(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agent/{agentId}")
    @Operation(summary = "Get areas by agent", description = "Retrieves all areas associated with a specific agent")
    @ApiResponse(responseCode = "200", description = "Agent areas retrieved successfully")
    public ResponseEntity<List<AgentAreasResponse>> getAgentAreasByAgent(@PathVariable Long agentId) {
        List<AgentAreasResponse> response = agentAreasService.getAgentAreasByAgent(agentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/area/{areaId}")
    @Operation(summary = "Get agents by area", description = "Retrieves all agents associated with a specific area")
    @ApiResponse(responseCode = "200", description = "Area agents retrieved successfully")
    public ResponseEntity<List<AgentAreasResponse>> getAgentAreasByArea(@PathVariable Long areaId) {
        List<AgentAreasResponse> response = agentAreasService.getAgentAreasByArea(areaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/state/{stateId}")
    @Operation(summary = "Get agent area associations by state", description = "Retrieves all associations filtered by state")
    @ApiResponse(responseCode = "200", description = "Agent area associations retrieved successfully")
    public ResponseEntity<List<AgentAreasResponse>> getAgentAreasByState(@PathVariable Long stateId) {
        List<AgentAreasResponse> response = agentAreasService.getAgentAreasByState(stateId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete agent area association", description = "Deletes a specific association between an agent and an area")
    @ApiResponse(responseCode = "204", description = "Agent area association deleted successfully")
    @ApiResponse(responseCode = "404", description = "Agent area association not found")
    public ResponseEntity<Void> deleteAgentAreas(@PathVariable Long id) {
        agentAreasService.deleteAgentAreas(id);
        return ResponseEntity.noContent().build();
    }
} 