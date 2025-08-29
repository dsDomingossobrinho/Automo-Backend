package com.automo.state.controller;

import com.automo.config.security.JwtUtils;
import com.automo.state.dto.StateDto;
import com.automo.state.response.StateResponse;
import com.automo.state.service.StateService;
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
@RequestMapping("/states")
@RequiredArgsConstructor
@Tag(name = "States", description = "State management APIs")
@SecurityRequirement(name = "bearerAuth")
public class StateController {

    private final StateService stateService;
    private final JwtUtils jwtUtils;

    @Operation(description = "List all states", summary = "Get all states")
    @ApiResponse(responseCode = "200", description = "States retrieved successfully")
    @GetMapping
    public ResponseEntity<List<StateResponse>> getAllStates() {
        return ResponseEntity.ok(stateService.getAllStates());
    }

    @Operation(description = "Get state by ID", summary = "Get a specific state by ID")
    @ApiResponse(responseCode = "200", description = "State retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<StateResponse> getStateById(@PathVariable Long id) {
        return ResponseEntity.ok(stateService.getStateByIdResponse(id));
    }

    @Operation(description = "Get state by name", summary = "Get a specific state by name")
    @ApiResponse(responseCode = "200", description = "State retrieved successfully")
    @GetMapping("/name/{stateName}")
    public ResponseEntity<StateResponse> getStateByName(@PathVariable String stateName) {
        return ResponseEntity.ok(stateService.getStateByIdResponse(stateService.getStateByState(stateName).getId()));
    }

    @Operation(description = "Create new state", summary = "Create a new state (Admin only)")
    @ApiResponse(responseCode = "201", description = "State created successfully")
    @PostMapping
    public ResponseEntity<StateResponse> createState(@Valid @RequestBody StateDto stateDto) {
        // Exemplo de verificação de permissão usando JwtUtils
        if (!jwtUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        StateResponse response = stateService.createState(stateDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update state", summary = "Update an existing state (Admin only)")
    @ApiResponse(responseCode = "200", description = "State updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<StateResponse> updateState(@PathVariable Long id, @Valid @RequestBody StateDto stateDto) {
        // Exemplo de verificação de permissão usando JwtUtils
        if (!jwtUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(stateService.updateState(id, stateDto));
    }

    @Operation(description = "Delete state", summary = "Delete a state (Admin only)")
    @ApiResponse(responseCode = "204", description = "State deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteState(@PathVariable Long id) {
        // Exemplo de verificação de permissão usando JwtUtils
        if (!jwtUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        stateService.deleteState(id);
        return ResponseEntity.noContent().build();
    }
} 