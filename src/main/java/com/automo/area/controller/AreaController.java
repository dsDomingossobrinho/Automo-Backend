package com.automo.area.controller;

import com.automo.area.dto.AreaDto;
import com.automo.area.response.AreaResponse;
import com.automo.area.service.AreaService;
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
@RequestMapping("/areas")
@RequiredArgsConstructor
@Tag(name = "Areas", description = "Area management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AreaController {

    private final AreaService areaService;

    @Operation(description = "List all areas", summary = "Get all areas")
    @ApiResponse(responseCode = "200", description = "Areas retrieved successfully")
    @GetMapping
    public ResponseEntity<List<AreaResponse>> getAllAreas() {
        return ResponseEntity.ok(areaService.getAllAreas());
    }

    @Operation(description = "Get area by ID", summary = "Get a specific area by ID")
    @ApiResponse(responseCode = "200", description = "Area retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<AreaResponse> getAreaById(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.getAreaByIdResponse(id));
    }

    @Operation(description = "Create new area", summary = "Create a new area")
    @ApiResponse(responseCode = "201", description = "Area created successfully")
    @PostMapping
    public ResponseEntity<AreaResponse> createArea(@Valid @RequestBody AreaDto areaDto) {
        AreaResponse response = areaService.createArea(areaDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update area", summary = "Update an existing area")
    @ApiResponse(responseCode = "200", description = "Area updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<AreaResponse> updateArea(@PathVariable Long id, @Valid @RequestBody AreaDto areaDto) {
        return ResponseEntity.ok(areaService.updateArea(id, areaDto));
    }

    @Operation(description = "Delete area", summary = "Delete an area")
    @ApiResponse(responseCode = "204", description = "Area deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable Long id) {
        areaService.deleteArea(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get areas by state", summary = "Get areas filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Areas retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<AreaResponse>> getAreasByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(areaService.getAreasByState(stateId));
    }
} 