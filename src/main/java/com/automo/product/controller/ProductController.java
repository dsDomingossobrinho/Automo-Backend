package com.automo.product.controller;

import com.automo.product.dto.ProductDto;
import com.automo.product.response.ProductResponse;
import com.automo.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @Operation(description = "List all products", summary = "Get all products")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(description = "Get product by ID", summary = "Get a specific product by ID")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductByIdResponse(id));
    }

    @Operation(description = "Create new product", summary = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductResponse response = productService.createProduct(productDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update product", summary = "Update an existing product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @Operation(description = "Delete product", summary = "Delete a product")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get products by state", summary = "Get products filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<ProductResponse>> getProductsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(productService.getProductsByState(stateId));
    }

    @Operation(description = "Upload product image", summary = "Upload an image file for a specific product")
    @ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid file type or size")
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<ProductResponse> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile) {
        
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            ProductResponse response = productService.uploadProductImage(id, imageFile);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 