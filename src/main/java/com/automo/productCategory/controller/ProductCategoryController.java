package com.automo.productCategory.controller;

import com.automo.productCategory.dto.ProductCategoryDto;
import com.automo.productCategory.response.ProductCategoryResponse;
import com.automo.productCategory.service.ProductCategoryService;
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
@RequestMapping("/product-categories")
@RequiredArgsConstructor
@Tag(name = "Product Categories", description = "Product category management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @Operation(description = "List all product categories", summary = "Get all product categories")
    @ApiResponse(responseCode = "200", description = "Product categories retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ProductCategoryResponse>> getAllProductCategories() {
        return ResponseEntity.ok(productCategoryService.getAllProductCategories());
    }

    @Operation(description = "Get product category by ID", summary = "Get a specific product category by ID")
    @ApiResponse(responseCode = "200", description = "Product category retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> getProductCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(productCategoryService.getProductCategoryByIdResponse(id));
    }

    @Operation(description = "Create new product category", summary = "Create a new product category")
    @ApiResponse(responseCode = "201", description = "Product category created successfully")
    @PostMapping
    public ResponseEntity<ProductCategoryResponse> createProductCategory(@Valid @RequestBody ProductCategoryDto productCategoryDto) {
        ProductCategoryResponse response = productCategoryService.createProductCategory(productCategoryDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update product category", summary = "Update an existing product category")
    @ApiResponse(responseCode = "200", description = "Product category updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> updateProductCategory(@PathVariable Long id, @Valid @RequestBody ProductCategoryDto productCategoryDto) {
        return ResponseEntity.ok(productCategoryService.updateProductCategory(id, productCategoryDto));
    }

    @Operation(description = "Delete product category", summary = "Delete a product category")
    @ApiResponse(responseCode = "204", description = "Product category deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductCategory(@PathVariable Long id) {
        productCategoryService.deleteProductCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get product categories by state", summary = "Get product categories filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Product categories retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<ProductCategoryResponse>> getProductCategoriesByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(productCategoryService.getProductCategoriesByState(stateId));
    }
} 