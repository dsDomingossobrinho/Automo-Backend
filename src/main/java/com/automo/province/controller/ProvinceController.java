package com.automo.province.controller;

import com.automo.province.dto.ProvinceDto;
import com.automo.province.response.ProvinceResponse;
import com.automo.province.service.ProvinceService;
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
@RequestMapping("/provinces")
@RequiredArgsConstructor
@Tag(name = "Provinces", description = "Province management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ProvinceController {

    private final ProvinceService provinceService;

    @Operation(description = "List all provinces", summary = "Get all provinces")
    @ApiResponse(responseCode = "200", description = "Provinces retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ProvinceResponse>> getAllProvinces() {
        return ResponseEntity.ok(provinceService.getAllProvinces());
    }

    @Operation(description = "Get province by ID", summary = "Get a specific province by ID")
    @ApiResponse(responseCode = "200", description = "Province retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<ProvinceResponse> getProvinceById(@PathVariable Long id) {
        return ResponseEntity.ok(provinceService.getProvinceByIdResponse(id));
    }

    @Operation(description = "Create new province", summary = "Create a new province")
    @ApiResponse(responseCode = "201", description = "Province created successfully")
    @PostMapping
    public ResponseEntity<ProvinceResponse> createProvince(@Valid @RequestBody ProvinceDto provinceDto) {
        ProvinceResponse response = provinceService.createProvince(provinceDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update province", summary = "Update an existing province")
    @ApiResponse(responseCode = "200", description = "Province updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<ProvinceResponse> updateProvince(@PathVariable Long id, @Valid @RequestBody ProvinceDto provinceDto) {
        return ResponseEntity.ok(provinceService.updateProvince(id, provinceDto));
    }

    @Operation(description = "Delete province", summary = "Delete a province")
    @ApiResponse(responseCode = "204", description = "Province deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvince(@PathVariable Long id) {
        provinceService.deleteProvince(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get provinces by country", summary = "Get provinces filtered by country ID")
    @ApiResponse(responseCode = "200", description = "Provinces retrieved successfully")
    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<ProvinceResponse>> getProvincesByCountry(@PathVariable Long countryId) {
        return ResponseEntity.ok(provinceService.getProvincesByCountry(countryId));
    }

    @Operation(description = "Get provinces by state", summary = "Get provinces filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Provinces retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<ProvinceResponse>> getProvincesByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(provinceService.getProvincesByState(stateId));
    }
} 