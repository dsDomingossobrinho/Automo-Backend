package com.automo.identifierType.controller;

import com.automo.identifierType.dto.IdentifierTypeDto;
import com.automo.identifierType.response.IdentifierTypeResponse;
import com.automo.identifierType.service.IdentifierTypeService;
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
@RequestMapping("/identifier-types")
@RequiredArgsConstructor
@Tag(name = "Identifier Types", description = "Identifier type management APIs")
@SecurityRequirement(name = "bearerAuth")
public class IdentifierTypeController {

    private final IdentifierTypeService identifierTypeService;

    @Operation(description = "List all identifier types", summary = "Get all identifier types")
    @ApiResponse(responseCode = "200", description = "Identifier types retrieved successfully")
    @GetMapping
    public ResponseEntity<List<IdentifierTypeResponse>> getAllIdentifierTypes() {
        return ResponseEntity.ok(identifierTypeService.getAllIdentifierTypes());
    }

    @Operation(description = "Get identifier type by ID", summary = "Get a specific identifier type by ID")
    @ApiResponse(responseCode = "200", description = "Identifier type retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<IdentifierTypeResponse> getIdentifierTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(identifierTypeService.getIdentifierTypeByIdResponse(id));
    }

    @Operation(description = "Create new identifier type", summary = "Create a new identifier type")
    @ApiResponse(responseCode = "201", description = "Identifier type created successfully")
    @PostMapping
    public ResponseEntity<IdentifierTypeResponse> createIdentifierType(@Valid @RequestBody IdentifierTypeDto identifierTypeDto) {
        IdentifierTypeResponse response = identifierTypeService.createIdentifierType(identifierTypeDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update identifier type", summary = "Update an existing identifier type")
    @ApiResponse(responseCode = "200", description = "Identifier type updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<IdentifierTypeResponse> updateIdentifierType(@PathVariable Long id, @Valid @RequestBody IdentifierTypeDto identifierTypeDto) {
        return ResponseEntity.ok(identifierTypeService.updateIdentifierType(id, identifierTypeDto));
    }

    @Operation(description = "Delete identifier type", summary = "Delete an identifier type")
    @ApiResponse(responseCode = "204", description = "Identifier type deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIdentifierType(@PathVariable Long id) {
        identifierTypeService.deleteIdentifierType(id);
        return ResponseEntity.noContent().build();
    }
} 