package com.automo.role.controller;

import com.automo.role.dto.RoleDto;
import com.automo.role.response.RoleResponse;
import com.automo.role.service.RoleService;
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
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role management APIs")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleService roleService;

    @Operation(description = "List all roles", summary = "Get all roles")
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @Operation(description = "Get role by ID", summary = "Get a specific role by ID")
    @ApiResponse(responseCode = "200", description = "Role retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleByIdResponse(id));
    }

    @Operation(description = "Create new role", summary = "Create a new role")
    @ApiResponse(responseCode = "201", description = "Role created successfully")
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleDto roleDto) {
        RoleResponse response = roleService.createRole(roleDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update role", summary = "Update an existing role")
    @ApiResponse(responseCode = "200", description = "Role updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDto));
    }

    @Operation(description = "Delete role", summary = "Delete a role")
    @ApiResponse(responseCode = "204", description = "Role deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
} 