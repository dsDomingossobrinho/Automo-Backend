package com.automo.associatedEmail.controller;

import com.automo.associatedEmail.dto.AssociatedEmailDto;
import com.automo.associatedEmail.response.AssociatedEmailResponse;
import com.automo.associatedEmail.service.AssociatedEmailService;
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
@RequestMapping("/associated-emails")
@RequiredArgsConstructor
@Tag(name = "Associated Emails", description = "Associated Email management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AssociatedEmailController {

    private final AssociatedEmailService associatedEmailService;

    @Operation(description = "List all associated emails", summary = "Get all associated emails")
    @ApiResponse(responseCode = "200", description = "Associated emails retrieved successfully")
    @GetMapping
    public ResponseEntity<List<AssociatedEmailResponse>> getAllAssociatedEmails() {
        return ResponseEntity.ok(associatedEmailService.getAllAssociatedEmails());
    }

    @Operation(description = "Get associated email by ID", summary = "Get a specific associated email by ID")
    @ApiResponse(responseCode = "200", description = "Associated email retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<AssociatedEmailResponse> getAssociatedEmailById(@PathVariable Long id) {
        return ResponseEntity.ok(associatedEmailService.getAssociatedEmailByIdResponse(id));
    }

    @Operation(description = "Get associated email by email address", summary = "Get a specific associated email by email address")
    @ApiResponse(responseCode = "200", description = "Associated email retrieved successfully")
    @GetMapping("/email/{email}")
    public ResponseEntity<AssociatedEmailResponse> getAssociatedEmailByEmail(@PathVariable String email) {
        return ResponseEntity.ok(associatedEmailService.getAssociatedEmailByEmail(email));
    }

    @Operation(description = "Create new associated email", summary = "Create a new associated email")
    @ApiResponse(responseCode = "201", description = "Associated email created successfully")
    @PostMapping
    public ResponseEntity<AssociatedEmailResponse> createAssociatedEmail(@Valid @RequestBody AssociatedEmailDto associatedEmailDto) {
        AssociatedEmailResponse response = associatedEmailService.createAssociatedEmail(associatedEmailDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update associated email", summary = "Update an existing associated email")
    @ApiResponse(responseCode = "200", description = "Associated email updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<AssociatedEmailResponse> updateAssociatedEmail(@PathVariable Long id, @Valid @RequestBody AssociatedEmailDto associatedEmailDto) {
        return ResponseEntity.ok(associatedEmailService.updateAssociatedEmail(id, associatedEmailDto));
    }

    @Operation(description = "Delete associated email", summary = "Delete an associated email")
    @ApiResponse(responseCode = "204", description = "Associated email deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssociatedEmail(@PathVariable Long id) {
        associatedEmailService.deleteAssociatedEmail(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get associated emails by identifier", summary = "Get associated emails filtered by identifier ID")
    @ApiResponse(responseCode = "200", description = "Associated emails retrieved successfully")
    @GetMapping("/identifier/{identifierId}")
    public ResponseEntity<List<AssociatedEmailResponse>> getAssociatedEmailsByIdentifier(@PathVariable Long identifierId) {
        return ResponseEntity.ok(associatedEmailService.getAssociatedEmailsByIdentifier(identifierId));
    }

    @Operation(description = "Get associated emails by state", summary = "Get associated emails filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Associated emails retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<AssociatedEmailResponse>> getAssociatedEmailsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(associatedEmailService.getAssociatedEmailsByState(stateId));
    }
} 