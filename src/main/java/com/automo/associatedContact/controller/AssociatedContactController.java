package com.automo.associatedContact.controller;

import com.automo.associatedContact.dto.AssociatedContactDto;
import com.automo.associatedContact.response.AssociatedContactResponse;
import com.automo.associatedContact.service.AssociatedContactService;
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
@RequestMapping("/associated-contacts")
@RequiredArgsConstructor
@Tag(name = "Associated Contacts", description = "Associated Contact management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AssociatedContactController {

    private final AssociatedContactService associatedContactService;

    @Operation(description = "List all associated contacts", summary = "Get all associated contacts")
    @ApiResponse(responseCode = "200", description = "Associated contacts retrieved successfully")
    @GetMapping
    public ResponseEntity<List<AssociatedContactResponse>> getAllAssociatedContacts() {
        return ResponseEntity.ok(associatedContactService.getAllAssociatedContacts());
    }

    @Operation(description = "Get associated contact by ID", summary = "Get a specific associated contact by ID")
    @ApiResponse(responseCode = "200", description = "Associated contact retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<AssociatedContactResponse> getAssociatedContactById(@PathVariable Long id) {
        return ResponseEntity.ok(associatedContactService.getAssociatedContactByIdResponse(id));
    }

    @Operation(description = "Create new associated contact", summary = "Create a new associated contact")
    @ApiResponse(responseCode = "201", description = "Associated contact created successfully")
    @PostMapping
    public ResponseEntity<AssociatedContactResponse> createAssociatedContact(@Valid @RequestBody AssociatedContactDto associatedContactDto) {
        AssociatedContactResponse response = associatedContactService.createAssociatedContact(associatedContactDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update associated contact", summary = "Update an existing associated contact")
    @ApiResponse(responseCode = "200", description = "Associated contact updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<AssociatedContactResponse> updateAssociatedContact(@PathVariable Long id, @Valid @RequestBody AssociatedContactDto associatedContactDto) {
        return ResponseEntity.ok(associatedContactService.updateAssociatedContact(id, associatedContactDto));
    }

    @Operation(description = "Delete associated contact", summary = "Delete an associated contact")
    @ApiResponse(responseCode = "204", description = "Associated contact deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssociatedContact(@PathVariable Long id) {
        associatedContactService.deleteAssociatedContact(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get associated contacts by identifier", summary = "Get associated contacts filtered by identifier ID")
    @ApiResponse(responseCode = "200", description = "Associated contacts retrieved successfully")
    @GetMapping("/identifier/{identifierId}")
    public ResponseEntity<List<AssociatedContactResponse>> getAssociatedContactsByIdentifier(@PathVariable Long identifierId) {
        return ResponseEntity.ok(associatedContactService.getAssociatedContactsByIdentifier(identifierId));
    }

    @Operation(description = "Get associated contacts by state", summary = "Get associated contacts filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Associated contacts retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<AssociatedContactResponse>> getAssociatedContactsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(associatedContactService.getAssociatedContactsByState(stateId));
    }
} 