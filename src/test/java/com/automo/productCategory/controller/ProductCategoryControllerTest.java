package com.automo.productCategory.controller;

import com.automo.productCategory.dto.ProductCategoryDto;
import com.automo.productCategory.response.ProductCategoryResponse;
import com.automo.productCategory.service.ProductCategoryService;
import com.automo.test.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductCategoryController.class)
@DisplayName("Tests for ProductCategoryController")
class ProductCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductCategoryService productCategoryService;

    private ProductCategoryResponse productCategoryResponse;
    private ProductCategoryDto productCategoryDto;

    @BeforeEach
    void setUp() {
        productCategoryResponse = new ProductCategoryResponse(
            1L,
            "Electronics",
            "Electronic devices and accessories",
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        productCategoryDto = TestDataFactory.createValidProductCategoryDto(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should get all product categories")
    void shouldGetAllProductCategories() throws Exception {
        // Given
        List<ProductCategoryResponse> responses = Arrays.asList(
            productCategoryResponse,
            new ProductCategoryResponse(2L, "Books", "Books and magazines", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now())
        );
        
        when(productCategoryService.getAllProductCategories()).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/product-categories"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].category").value("Electronics"))
            .andExpect(jsonPath("$[0].description").value("Electronic devices and accessories"))
            .andExpect(jsonPath("$[0].stateId").value(1L))
            .andExpect(jsonPath("$[0].stateName").value("ACTIVE"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].category").value("Books"))
            .andExpect(jsonPath("$[1].description").value("Books and magazines"));

        verify(productCategoryService).getAllProductCategories();
    }

    @Test
    @WithMockUser
    @DisplayName("Should get product category by ID")
    void shouldGetProductCategoryById() throws Exception {
        // Given
        when(productCategoryService.getProductCategoryByIdResponse(1L)).thenReturn(productCategoryResponse);

        // When & Then
        mockMvc.perform(get("/product-categories/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.category").value("Electronics"))
            .andExpect(jsonPath("$.description").value("Electronic devices and accessories"))
            .andExpect(jsonPath("$.stateId").value(1L))
            .andExpect(jsonPath("$.stateName").value("ACTIVE"));

        verify(productCategoryService).getProductCategoryByIdResponse(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when product category not found")
    void shouldReturn404WhenProductCategoryNotFound() throws Exception {
        // Given
        when(productCategoryService.getProductCategoryByIdResponse(99L))
            .thenThrow(new EntityNotFoundException("ProductCategory with ID 99 not found"));

        // When & Then
        mockMvc.perform(get("/product-categories/99"))
            .andExpect(status().isNotFound());

        verify(productCategoryService).getProductCategoryByIdResponse(99L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should create product category successfully")
    void shouldCreateProductCategorySuccessfully() throws Exception {
        // Given
        when(productCategoryService.createProductCategory(any(ProductCategoryDto.class)))
            .thenReturn(productCategoryResponse);

        // When & Then
        mockMvc.perform(post("/product-categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCategoryDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.category").value("Electronics"))
            .andExpect(jsonPath("$.description").value("Electronic devices and accessories"));

        verify(productCategoryService).createProductCategory(any(ProductCategoryDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when creating product category with invalid data")
    void shouldReturn400WhenCreatingProductCategoryWithInvalidData() throws Exception {
        // Given
        ProductCategoryDto invalidDto = new ProductCategoryDto("", "Description", 1L);

        // When & Then
        mockMvc.perform(post("/product-categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(productCategoryService, never()).createProductCategory(any(ProductCategoryDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when creating product category with null category")
    void shouldReturn400WhenCreatingProductCategoryWithNullCategory() throws Exception {
        // Given
        ProductCategoryDto invalidDto = new ProductCategoryDto(null, "Description", 1L);

        // When & Then
        mockMvc.perform(post("/product-categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(productCategoryService, never()).createProductCategory(any(ProductCategoryDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when creating product category with null state ID")
    void shouldReturn400WhenCreatingProductCategoryWithNullStateId() throws Exception {
        // Given
        ProductCategoryDto invalidDto = new ProductCategoryDto("Electronics", "Description", null);

        // When & Then
        mockMvc.perform(post("/product-categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(productCategoryService, never()).createProductCategory(any(ProductCategoryDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should update product category successfully")
    void shouldUpdateProductCategorySuccessfully() throws Exception {
        // Given
        ProductCategoryResponse updatedResponse = new ProductCategoryResponse(
            1L, "Updated Electronics", "Updated description", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );
        
        when(productCategoryService.updateProductCategory(eq(1L), any(ProductCategoryDto.class)))
            .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/product-categories/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCategoryDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.category").value("Updated Electronics"))
            .andExpect(jsonPath("$.description").value("Updated description"));

        verify(productCategoryService).updateProductCategory(eq(1L), any(ProductCategoryDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when updating non-existent product category")
    void shouldReturn404WhenUpdatingNonExistentProductCategory() throws Exception {
        // Given
        when(productCategoryService.updateProductCategory(eq(99L), any(ProductCategoryDto.class)))
            .thenThrow(new EntityNotFoundException("ProductCategory with ID 99 not found"));

        // When & Then
        mockMvc.perform(put("/product-categories/99")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCategoryDto)))
            .andExpect(status().isNotFound());

        verify(productCategoryService).updateProductCategory(eq(99L), any(ProductCategoryDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when updating with invalid data")
    void shouldReturn400WhenUpdatingWithInvalidData() throws Exception {
        // Given
        ProductCategoryDto invalidDto = new ProductCategoryDto("", "Description", 1L);

        // When & Then
        mockMvc.perform(put("/product-categories/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());

        verify(productCategoryService, never()).updateProductCategory(any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should delete product category successfully")
    void shouldDeleteProductCategorySuccessfully() throws Exception {
        // Given
        doNothing().when(productCategoryService).deleteProductCategory(1L);

        // When & Then
        mockMvc.perform(delete("/product-categories/1")
                .with(csrf()))
            .andExpect(status().isNoContent());

        verify(productCategoryService).deleteProductCategory(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 404 when deleting non-existent product category")
    void shouldReturn404WhenDeletingNonExistentProductCategory() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("ProductCategory with ID 99 not found"))
            .when(productCategoryService).deleteProductCategory(99L);

        // When & Then
        mockMvc.perform(delete("/product-categories/99")
                .with(csrf()))
            .andExpect(status().isNotFound());

        verify(productCategoryService).deleteProductCategory(99L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should get product categories by state")
    void shouldGetProductCategoriesByState() throws Exception {
        // Given
        List<ProductCategoryResponse> responses = Arrays.asList(productCategoryResponse);
        when(productCategoryService.getProductCategoriesByState(1L)).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/product-categories/state/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].category").value("Electronics"))
            .andExpect(jsonPath("$[0].stateId").value(1L));

        verify(productCategoryService).getProductCategoriesByState(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("Should return empty list when no categories found for state")
    void shouldReturnEmptyListWhenNoCategoriesFoundForState() throws Exception {
        // Given
        when(productCategoryService.getProductCategoriesByState(99L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/product-categories/state/99"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));

        verify(productCategoryService).getProductCategoriesByState(99L);
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/product-categories"))
            .andExpect(status().isUnauthorized());

        verify(productCategoryService, never()).getAllProductCategories();
    }

    @Test
    @DisplayName("Should return 403 when CSRF token missing on POST")
    void shouldReturn403WhenCsrfTokenMissingOnPost() throws Exception {
        // When & Then
        mockMvc.perform(post("/product-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productCategoryDto)))
            .andExpect(status().isForbidden());

        verify(productCategoryService, never()).createProductCategory(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle malformed JSON")
    void shouldHandleMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/product-categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid-json"))
            .andExpect(status().isBadRequest());

        verify(productCategoryService, never()).createProductCategory(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle missing request body")
    void shouldHandleMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/product-categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(productCategoryService, never()).createProductCategory(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle invalid path parameter")
    void shouldHandleInvalidPathParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/product-categories/invalid"))
            .andExpect(status().isBadRequest());

        verify(productCategoryService, never()).getProductCategoryByIdResponse(any());
    }
}