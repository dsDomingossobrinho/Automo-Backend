package com.automo.user.controller;

import com.automo.user.dto.UserDto;
import com.automo.user.response.UserResponse;
import com.automo.user.service.UserService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(description = "List all users", summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(description = "Get user by ID", summary = "Get a specific user by ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserByIdResponse(id));
    }

    @Operation(description = "Get user by email", summary = "Get a specific user by email")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @Operation(description = "Get user by auth ID", summary = "Get a specific user by authentication ID")
    @ApiResponse(responseCode = "200", description = "User retrieved successfully")
    @GetMapping("/auth/{authId}")
    public ResponseEntity<UserResponse> getUserByAuthId(@PathVariable Long authId) {
        return ResponseEntity.ok(userService.getUserByAuthId(authId));
    }

    @Operation(description = "Create new user", summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserDto userDto) {
        UserResponse response = userService.createUser(userDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update user", summary = "Update an existing user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @Operation(description = "Delete user", summary = "Delete a user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get users by state", summary = "Get users filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<UserResponse>> getUsersByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(userService.getUsersByState(stateId));
    }

    @Operation(description = "Get users by country", summary = "Get users filtered by country ID")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<UserResponse>> getUsersByCountry(@PathVariable Long countryId) {
        return ResponseEntity.ok(userService.getUsersByCountry(countryId));
    }

    @Operation(description = "Get users by organization type", summary = "Get users filtered by organization type ID")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping("/organization-type/{organizationTypeId}")
    public ResponseEntity<List<UserResponse>> getUsersByOrganizationType(@PathVariable Long organizationTypeId) {
        return ResponseEntity.ok(userService.getUsersByOrganizationType(organizationTypeId));
    }

    @Operation(description = "Get users by province", summary = "Get users filtered by province ID")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping("/province/{provinceId}")
    public ResponseEntity<List<UserResponse>> getUsersByProvince(@PathVariable Long provinceId) {
        return ResponseEntity.ok(userService.getUsersByProvince(provinceId));
    }
} 