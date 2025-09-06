package com.automo.product.controller;

import com.automo.config.security.JwtUtils;
import com.automo.product.dto.ProductDto;
import com.automo.product.response.ProductResponse;
import com.automo.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
@DisplayName("Tests for ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDto productDto;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productDto = new ProductDto(
            "Test Product", 
            "test-image.jpg", 
            "Test product description", 
            new BigDecimal("299.99"), 
            1L
        );
        
        productResponse = new ProductResponse(
            1L,
            "Test Product",
            "test-image.jpg",
            "Test product description",
            new BigDecimal("299.99"),
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser
    @DisplayName("Should get all products successfully")
    void shouldGetAllProductsSuccessfully() throws Exception {
        // Given
        ProductResponse product1 = new ProductResponse(
            1L, "Product 1", "image1.jpg", "Description 1", 
            new BigDecimal("100.00"), 1L, "ACTIVE", 
            LocalDateTime.now(), LocalDateTime.now()
        );
        ProductResponse product2 = new ProductResponse(
            2L, "Product 2", "image2.jpg", "Description 2", 
            new BigDecimal("200.00"), 1L, "ACTIVE", 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        List<ProductResponse> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get product by id successfully")
    void shouldGetProductByIdSuccessfully() throws Exception {
        // Given
        when(productService.getProductByIdResponse(1L)).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(299.99))
                .andExpect(jsonPath("$.description").value("Test product description"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        // Given
        when(productService.createProduct(any(ProductDto.class))).thenReturn(productResponse);

        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(299.99));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        // Given
        ProductResponse updatedProduct = new ProductResponse(
            1L, "Updated Product", "updated-image.jpg", "Updated description", 
            new BigDecimal("399.99"), 1L, "ACTIVE", 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(productService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(updatedProduct);

        ProductDto updateDto = new ProductDto(
            "Updated Product", "updated-image.jpg", "Updated description", 
            new BigDecimal("399.99"), 1L
        );

        // When & Then
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(399.99));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/products/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("Should get products by state successfully")
    void shouldGetProductsByStateSuccessfully() throws Exception {
        // Given
        List<ProductResponse> products = Arrays.asList(productResponse);
        when(productService.getProductsByState(1L)).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/products/state/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should upload product image successfully")
    void shouldUploadProductImageSuccessfully() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
            "image", "test-image.jpg", "image/jpeg", "test image content".getBytes()
        );
        
        ProductResponse updatedProduct = new ProductResponse(
            1L, "Test Product", "new-uploaded-image.jpg", "Test product description", 
            new BigDecimal("299.99"), 1L, "ACTIVE", 
            LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(productService.uploadProductImage(eq(1L), any())).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(multipart("/products/1/upload-image")
                .file(imageFile)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.img").value("new-uploaded-image.jpg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request for empty image file")
    void shouldReturnBadRequestForEmptyImageFile() throws Exception {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile(
            "image", "empty.jpg", "image/jpeg", new byte[0]
        );

        // When & Then
        mockMvc.perform(multipart("/products/1/upload-image")
                .file(emptyFile)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request when upload fails")
    void shouldReturnBadRequestWhenUploadFails() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
            "image", "test-image.jpg", "image/jpeg", "test image content".getBytes()
        );
        
        when(productService.uploadProductImage(eq(1L), any()))
            .thenThrow(new RuntimeException("Upload failed"));

        // When & Then
        mockMvc.perform(multipart("/products/1/upload-image")
                .file(imageFile)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 401 for unauthorized access")
    void shouldReturn401ForUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden create operation")
    void shouldReturn403ForForbiddenCreateOperation() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden update operation")
    void shouldReturn403ForForbiddenUpdateOperation() throws Exception {
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden delete operation")
    void shouldReturn403ForForbiddenDeleteOperation() throws Exception {
        mockMvc.perform(delete("/products/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid product data")
    void shouldReturn400ForInvalidProductData() throws Exception {
        // Given
        ProductDto invalidProduct = new ProductDto(
            "", // Invalid empty name
            "image.jpg", 
            "Description", 
            new BigDecimal("-100.00"), // Invalid negative price
            1L
        );

        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}