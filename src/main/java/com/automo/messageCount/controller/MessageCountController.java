package com.automo.messageCount.controller;

import com.automo.messageCount.dto.MessageCountDto;
import com.automo.messageCount.response.MessageCountResponse;
import com.automo.messageCount.service.MessageCountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message-counts")
@RequiredArgsConstructor
@Tag(name = "Message Counts", description = "Message count management APIs")
public class MessageCountController {

    private final MessageCountService messageCountService;

    @GetMapping
    @Operation(description = "List all message counts", summary = "Get all message counts")
    @ApiResponse(responseCode = "200", description = "Message counts retrieved successfully")
    public ResponseEntity<List<MessageCountResponse>> getAllMessageCounts() {
        return ResponseEntity.ok(messageCountService.getAllMessageCounts());
    }

    @GetMapping("/{id}")
    @Operation(description = "Get message count by ID", summary = "Get a specific message count by ID")
    @ApiResponse(responseCode = "200", description = "Message count retrieved successfully")
    public ResponseEntity<MessageCountResponse> getMessageCountById(@PathVariable Long id) {
        return ResponseEntity.ok(messageCountService.getMessageCountByIdResponse(id));
    }

    @PostMapping
    @Operation(description = "Create new message count", summary = "Create a new message count")
    @ApiResponse(responseCode = "201", description = "Message count created successfully")
    public ResponseEntity<MessageCountResponse> createMessageCount(@Valid @RequestBody MessageCountDto messageCountDto) {
        MessageCountResponse response = messageCountService.createMessageCount(messageCountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(description = "Update message count", summary = "Update an existing message count")
    @ApiResponse(responseCode = "200", description = "Message count updated successfully")
    public ResponseEntity<MessageCountResponse> updateMessageCount(@PathVariable Long id, @Valid @RequestBody MessageCountDto messageCountDto) {
        return ResponseEntity.ok(messageCountService.updateMessageCount(id, messageCountDto));
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Delete message count", summary = "Delete a message count")
    @ApiResponse(responseCode = "204", description = "Message count deleted successfully")
    public ResponseEntity<Void> deleteMessageCount(@PathVariable Long id) {
        messageCountService.deleteMessageCount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lead/{leadId}")
    @Operation(description = "Get message counts by lead", summary = "Get message counts filtered by lead ID")
    @ApiResponse(responseCode = "200", description = "Message counts retrieved successfully")
    public ResponseEntity<List<MessageCountResponse>> getMessageCountsByLead(@PathVariable Long leadId) {
        return ResponseEntity.ok(messageCountService.getMessageCountsByLead(leadId));
    }

    @GetMapping("/state/{stateId}")
    @Operation(description = "Get message counts by state", summary = "Get message counts filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Message counts retrieved successfully")
    public ResponseEntity<List<MessageCountResponse>> getMessageCountsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(messageCountService.getMessageCountsByState(stateId));
    }
} 