package com.automo.dealProduct.controller;

import com.automo.dealProduct.dto.DealProductDto;
import com.automo.dealProduct.response.DealProductResponse;
import com.automo.dealProduct.service.DealProductService;
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
@RequestMapping("/deal-products")
@RequiredArgsConstructor
@Tag(name = "Deal Products", description = "Deal Product management APIs")
@SecurityRequirement(name = "bearerAuth")
public class DealProductController {

    private final DealProductService dealProductService;

    @Operation(description = "List all deal products", summary = "Get all deal products")
    @ApiResponse(responseCode = "200", description = "Deal products retrieved successfully")
    @GetMapping
    public ResponseEntity<List<DealProductResponse>> getAllDealProducts() {
        return ResponseEntity.ok(dealProductService.getAllDealProducts());
    }

    @Operation(description = "Get deal product by ID", summary = "Get a specific deal product by ID")
    @ApiResponse(responseCode = "200", description = "Deal product retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<DealProductResponse> getDealProductById(@PathVariable Long id) {
        return ResponseEntity.ok(dealProductService.getDealProductByIdResponse(id));
    }

    @Operation(description = "Create new deal product", summary = "Create a new deal product")
    @ApiResponse(responseCode = "201", description = "Deal product created successfully")
    @PostMapping
    public ResponseEntity<DealProductResponse> createDealProduct(@Valid @RequestBody DealProductDto dealProductDto) {
        DealProductResponse response = dealProductService.createDealProduct(dealProductDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update deal product", summary = "Update an existing deal product")
    @ApiResponse(responseCode = "200", description = "Deal product updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<DealProductResponse> updateDealProduct(@PathVariable Long id, @Valid @RequestBody DealProductDto dealProductDto) {
        return ResponseEntity.ok(dealProductService.updateDealProduct(id, dealProductDto));
    }

    @Operation(description = "Delete deal product", summary = "Delete a deal product")
    @ApiResponse(responseCode = "204", description = "Deal product deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDealProduct(@PathVariable Long id) {
        dealProductService.deleteDealProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get deal products by state", summary = "Get deal products filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Deal products retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<DealProductResponse>> getDealProductsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(dealProductService.getDealProductsByState(stateId));
    }

    @Operation(description = "Get deal products by deal", summary = "Get deal products filtered by deal ID")
    @ApiResponse(responseCode = "200", description = "Deal products retrieved successfully")
    @GetMapping("/deal/{dealId}")
    public ResponseEntity<List<DealProductResponse>> getDealProductsByDeal(@PathVariable Long dealId) {
        return ResponseEntity.ok(dealProductService.getDealProductsByDeal(dealId));
    }

    @Operation(description = "Get deal products by product", summary = "Get deal products filtered by product ID")
    @ApiResponse(responseCode = "200", description = "Deal products retrieved successfully")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<DealProductResponse>> getDealProductsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(dealProductService.getDealProductsByProduct(productId));
    }
} 