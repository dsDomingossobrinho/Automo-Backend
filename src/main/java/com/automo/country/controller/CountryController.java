package com.automo.country.controller;

import com.automo.country.dto.CountryDto;
import com.automo.country.response.CountryResponse;
import com.automo.country.service.CountryService;
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
@RequestMapping("/countries")
@RequiredArgsConstructor
@Tag(name = "Countries", description = "Country management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CountryController {

    private final CountryService countryService;

    @Operation(description = "List all countries", summary = "Get all countries")
    @ApiResponse(responseCode = "200", description = "Countries retrieved successfully")
    @GetMapping
    public ResponseEntity<List<CountryResponse>> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    @Operation(description = "Get country by ID", summary = "Get a specific country by ID")
    @ApiResponse(responseCode = "200", description = "Country retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> getCountryById(@PathVariable Long id) {
        return ResponseEntity.ok(countryService.getCountryByIdResponse(id));
    }

    @Operation(description = "Create new country", summary = "Create a new country")
    @ApiResponse(responseCode = "201", description = "Country created successfully")
    @PostMapping
    public ResponseEntity<CountryResponse> createCountry(@Valid @RequestBody CountryDto countryDto) {
        CountryResponse response = countryService.createCountry(countryDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update country", summary = "Update an existing country")
    @ApiResponse(responseCode = "200", description = "Country updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<CountryResponse> updateCountry(@PathVariable Long id, @Valid @RequestBody CountryDto countryDto) {
        return ResponseEntity.ok(countryService.updateCountry(id, countryDto));
    }

    @Operation(description = "Delete country", summary = "Delete a country")
    @ApiResponse(responseCode = "204", description = "Country deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get countries by state", summary = "Get countries filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Countries retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<CountryResponse>> getCountriesByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(countryService.getCountriesByState(stateId));
    }
} 