package com.automo.authRoles.controller;

import com.automo.authRoles.dto.AuthRolesDto;
import com.automo.authRoles.response.AuthRolesResponse;
import com.automo.authRoles.service.AuthRolesService;
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
@RequestMapping("/auth-roles")
@RequiredArgsConstructor
@Tag(name = "Auth Roles", description = "Auth Roles management APIs")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class AuthRolesController {

    private final AuthRolesService authRolesService;

    @Operation(description = "Create new auth roles association", summary = "Create a new auth roles association")
    @ApiResponse(responseCode = "201", description = "Auth roles association created successfully")
    @PostMapping
    public ResponseEntity<AuthRolesResponse> createAuthRoles(@Valid @RequestBody AuthRolesDto authRolesDto) {
        AuthRolesResponse response = authRolesService.createAuthRoles(authRolesDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(description = "Get auth roles by ID", summary = "Get a specific auth roles association by ID")
    @ApiResponse(responseCode = "200", description = "Auth roles association retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<AuthRolesResponse> getAuthRolesById(@PathVariable Long id) {
        AuthRolesResponse response = authRolesService.getAuthRolesById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "Get all auth roles associations", summary = "Get all auth roles associations")
    @ApiResponse(responseCode = "200", description = "Auth roles associations retrieved successfully")
    @GetMapping
    public ResponseEntity<List<AuthRolesResponse>> getAllAuthRoles() {
        List<AuthRolesResponse> response = authRolesService.getAllAuthRoles();
        return ResponseEntity.ok(response);
    }

    @Operation(description = "Update auth roles association", summary = "Update an existing auth roles association")
    @ApiResponse(responseCode = "200", description = "Auth roles association updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<AuthRolesResponse> updateAuthRoles(@PathVariable Long id, @Valid @RequestBody AuthRolesDto authRolesDto) {
        AuthRolesResponse response = authRolesService.updateAuthRoles(id, authRolesDto);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "Delete auth roles association", summary = "Delete an auth roles association")
    @ApiResponse(responseCode = "204", description = "Auth roles association deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthRoles(@PathVariable Long id) {
        authRolesService.deleteAuthRoles(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get auth roles by auth ID", summary = "Get all roles for a specific auth user")
    @ApiResponse(responseCode = "200", description = "Auth roles retrieved successfully")
    @GetMapping("/auth/{authId}")
    public ResponseEntity<List<AuthRolesResponse>> getAuthRolesByAuthId(@PathVariable Long authId) {
        List<AuthRolesResponse> response = authRolesService.getAuthRolesByAuthId(authId);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "Get auth roles by role ID", summary = "Get all auth users with a specific role")
    @ApiResponse(responseCode = "200", description = "Auth roles retrieved successfully")
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<AuthRolesResponse>> getAuthRolesByRoleId(@PathVariable Long roleId) {
        List<AuthRolesResponse> response = authRolesService.getAuthRolesByRoleId(roleId);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "Get auth roles by state ID", summary = "Get all auth roles with a specific state")
    @ApiResponse(responseCode = "200", description = "Auth roles retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<AuthRolesResponse>> getAuthRolesByStateId(@PathVariable Long stateId) {
        List<AuthRolesResponse> response = authRolesService.getAuthRolesByStateId(stateId);
        return ResponseEntity.ok(response);
    }
} 