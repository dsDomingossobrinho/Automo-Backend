package com.automo.identifier.controller;

import com.automo.identifier.dto.IdentifierDto;
import com.automo.identifier.response.IdentifierResponse;
import com.automo.identifier.service.IdentifierService;
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
@RequestMapping("/identifiers")
@RequiredArgsConstructor
@Tag(name = "Identifiers", description = "Identifier management APIs")
@SecurityRequirement(name = "bearerAuth")
public class IdentifierController {

    private final IdentifierService identifierService;

    @Operation(description = "List all identifiers", summary = "Get all identifiers")
    @ApiResponse(responseCode = "200", description = "Identifiers retrieved successfully")
    @GetMapping
    public ResponseEntity<List<IdentifierResponse>> getAllIdentifiers() {
        return ResponseEntity.ok(identifierService.getAllIdentifiers());
    }

    @Operation(description = "Get identifier by ID", summary = "Get a specific identifier by ID")
    @ApiResponse(responseCode = "200", description = "Identifier retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<IdentifierResponse> getIdentifierById(@PathVariable Long id) {
        return ResponseEntity.ok(identifierService.getIdentifierByIdResponse(id));
    }

    @Operation(description = "Create new identifier", summary = "Create a new identifier")
    @ApiResponse(responseCode = "201", description = "Identifier created successfully")
    @PostMapping
    public ResponseEntity<IdentifierResponse> createIdentifier(@Valid @RequestBody IdentifierDto identifierDto) {
        IdentifierResponse response = identifierService.createIdentifier(identifierDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update identifier", summary = "Update an existing identifier")
    @ApiResponse(responseCode = "200", description = "Identifier updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<IdentifierResponse> updateIdentifier(@PathVariable Long id, @Valid @RequestBody IdentifierDto identifierDto) {
        return ResponseEntity.ok(identifierService.updateIdentifier(id, identifierDto));
    }

    @Operation(description = "Delete identifier", summary = "Delete an identifier")
    @ApiResponse(responseCode = "204", description = "Identifier deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIdentifier(@PathVariable Long id) {
        identifierService.deleteIdentifier(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get identifiers by state", summary = "Get identifiers filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Identifiers retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<IdentifierResponse>> getIdentifiersByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(identifierService.getIdentifiersByState(stateId));
    }

    @Operation(description = "Get identifiers by user", summary = "Get identifiers filtered by user ID")
    @ApiResponse(responseCode = "200", description = "Identifiers retrieved successfully")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IdentifierResponse>> getIdentifiersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(identifierService.getIdentifiersByUser(userId));
    }

    @Operation(description = "Get identifiers by type", summary = "Get identifiers filtered by identifier type ID")
    @ApiResponse(responseCode = "200", description = "Identifiers retrieved successfully")
    @GetMapping("/type/{identifierTypeId}")
    public ResponseEntity<List<IdentifierResponse>> getIdentifiersByType(@PathVariable Long identifierTypeId) {
        return ResponseEntity.ok(identifierService.getIdentifiersByType(identifierTypeId));
    }
} 