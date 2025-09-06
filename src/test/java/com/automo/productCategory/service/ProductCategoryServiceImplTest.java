package com.automo.productCategory.service;

import com.automo.productCategory.dto.ProductCategoryDto;
import com.automo.productCategory.entity.ProductCategory;
import com.automo.productCategory.repository.ProductCategoryRepository;
import com.automo.productCategory.response.ProductCategoryResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for ProductCategoryServiceImpl")
class ProductCategoryServiceImplTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private StateService stateService;

    @InjectMocks
    private ProductCategoryServiceImpl productCategoryService;

    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private ProductCategory productCategory;
    private ProductCategoryDto productCategoryDto;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        inactiveState = TestDataFactory.createInactiveState();
        inactiveState.setId(2L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(3L);

        productCategory = TestDataFactory.createValidProductCategory(activeState);
        productCategory.setId(1L);
        productCategory.setCreatedAt(LocalDateTime.now());
        productCategory.setUpdatedAt(LocalDateTime.now());

        productCategoryDto = TestDataFactory.createValidProductCategoryDto(activeState.getId());
    }

    @Test
    @DisplayName("Should create ProductCategory successfully")
    void shouldCreateProductCategorySuccessfully() {
        // Given
        when(stateService.findById(activeState.getId())).thenReturn(activeState);
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(productCategory);

        // When
        ProductCategoryResponse response = productCategoryService.createProductCategory(productCategoryDto);

        // Then
        assertNotNull(response);
        assertEquals(productCategory.getId(), response.id());
        assertEquals(productCategory.getCategory(), response.category());
        assertEquals(productCategory.getDescription(), response.description());
        assertEquals(activeState.getId(), response.stateId());
        assertEquals(activeState.getState(), response.stateName());

        verify(stateService).findById(activeState.getId());
        verify(productCategoryRepository).save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should throw exception when state not found during creation")
    void shouldThrowExceptionWhenStateNotFoundDuringCreation() {
        // Given
        when(stateService.findById(activeState.getId())).thenThrow(new EntityNotFoundException("State not found"));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            productCategoryService.createProductCategory(productCategoryDto);
        });

        verify(stateService).findById(activeState.getId());
        verify(productCategoryRepository, never()).save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should update ProductCategory successfully")
    void shouldUpdateProductCategorySuccessfully() {
        // Given
        ProductCategoryDto updateDto = new ProductCategoryDto("Updated Category", "Updated description", activeState.getId());
        
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));
        when(stateService.findById(activeState.getId())).thenReturn(activeState);
        
        ProductCategory updatedCategory = TestDataFactory.createValidProductCategory(activeState);
        updatedCategory.setId(1L);
        updatedCategory.setCategory("Updated Category");
        updatedCategory.setDescription("Updated description");
        
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(updatedCategory);

        // When
        ProductCategoryResponse response = productCategoryService.updateProductCategory(1L, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Updated Category", response.category());
        assertEquals("Updated description", response.description());

        verify(productCategoryRepository).findById(1L);
        verify(stateService).findById(activeState.getId());
        verify(productCategoryRepository).save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should throw exception when ProductCategory not found during update")
    void shouldThrowExceptionWhenProductCategoryNotFoundDuringUpdate() {
        // Given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            productCategoryService.updateProductCategory(1L, productCategoryDto);
        });

        verify(productCategoryRepository).findById(1L);
        verify(stateService, never()).findById(any());
        verify(productCategoryRepository, never()).save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should get all ProductCategories excluding eliminated ones")
    void shouldGetAllProductCategoriesExcludingEliminatedOnes() {
        // Given
        ProductCategory category1 = TestDataFactory.createValidProductCategory(activeState);
        category1.setId(1L);
        category1.setCategory("Category 1");
        
        ProductCategory category2 = TestDataFactory.createValidProductCategory(inactiveState);
        category2.setId(2L);
        category2.setCategory("Category 2");
        
        ProductCategory eliminatedCategory = TestDataFactory.createValidProductCategory(eliminatedState);
        eliminatedCategory.setId(3L);
        eliminatedCategory.setCategory("Eliminated Category");

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(productCategoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2, eliminatedCategory));

        // When
        List<ProductCategoryResponse> responses = productCategoryService.getAllProductCategories();

        // Then
        assertEquals(2, responses.size());
        assertEquals("Category 1", responses.get(0).category());
        assertEquals("Category 2", responses.get(1).category());
        
        // Verify eliminated category is not in the results
        assertTrue(responses.stream().noneMatch(r -> r.category().equals("Eliminated Category")));

        verify(stateService).getEliminatedState();
        verify(productCategoryRepository).findAll();
    }

    @Test
    @DisplayName("Should get ProductCategory by ID")
    void shouldGetProductCategoryById() {
        // Given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));

        // When
        ProductCategory result = productCategoryService.getProductCategoryById(1L);

        // Then
        assertNotNull(result);
        assertEquals(productCategory.getId(), result.getId());
        assertEquals(productCategory.getCategory(), result.getCategory());

        verify(productCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when ProductCategory not found by ID")
    void shouldThrowExceptionWhenProductCategoryNotFoundById() {
        // Given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            productCategoryService.getProductCategoryById(1L);
        });

        verify(productCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get ProductCategory by ID as response")
    void shouldGetProductCategoryByIdAsResponse() {
        // Given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));

        // When
        ProductCategoryResponse response = productCategoryService.getProductCategoryByIdResponse(1L);

        // Then
        assertNotNull(response);
        assertEquals(productCategory.getId(), response.id());
        assertEquals(productCategory.getCategory(), response.category());
        assertEquals(productCategory.getDescription(), response.description());

        verify(productCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get ProductCategories by state")
    void shouldGetProductCategoriesByState() {
        // Given
        List<ProductCategory> categories = Arrays.asList(productCategory);
        when(productCategoryRepository.findByStateId(activeState.getId())).thenReturn(categories);

        // When
        List<ProductCategoryResponse> responses = productCategoryService.getProductCategoriesByState(activeState.getId());

        // Then
        assertEquals(1, responses.size());
        assertEquals(productCategory.getCategory(), responses.get(0).category());

        verify(productCategoryRepository).findByStateId(activeState.getId());
    }

    @Test
    @DisplayName("Should delete ProductCategory with soft delete")
    void shouldDeleteProductCategoryWithSoftDelete() {
        // Given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(productCategory);

        // When
        productCategoryService.deleteProductCategory(1L);

        // Then
        verify(productCategoryRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(productCategoryRepository).save(argThat(category -> 
            category.getState().equals(eliminatedState)));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent ProductCategory")
    void shouldThrowExceptionWhenDeletingNonExistentProductCategory() {
        // Given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            productCategoryService.deleteProductCategory(1L);
        });

        verify(productCategoryRepository).findById(1L);
        verify(stateService, never()).getEliminatedState();
        verify(productCategoryRepository, never()).save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should find ProductCategory by ID")
    void shouldFindProductCategoryById() {
        // Given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));

        // When
        ProductCategory result = productCategoryService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(productCategory, result);

        verify(productCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find ProductCategory by ID and state ID")
    void shouldFindProductCategoryByIdAndStateId() {
        // Given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));

        // When
        ProductCategory result = productCategoryService.findByIdAndStateId(1L, activeState.getId());

        // Then
        assertNotNull(result);
        assertEquals(productCategory, result);

        verify(productCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should use default state when state ID is null")
    void shouldUseDefaultStateWhenStateIdIsNull() {
        // Given
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));

        // When
        ProductCategory result = productCategoryService.findByIdAndStateId(1L, null);

        // Then
        assertNotNull(result);
        assertEquals(productCategory, result);

        verify(productCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when state doesn't match")
    void shouldThrowExceptionWhenStateDoesntMatch() {
        // Given
        ProductCategory categoryWithDifferentState = TestDataFactory.createValidProductCategory(inactiveState);
        categoryWithDifferentState.setId(1L);
        
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(categoryWithDifferentState));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            productCategoryService.findByIdAndStateId(1L, activeState.getId());
        });

        verify(productCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle empty repository when getting all categories")
    void shouldHandleEmptyRepositoryWhenGettingAllCategories() {
        // Given
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(productCategoryRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<ProductCategoryResponse> responses = productCategoryService.getAllProductCategories();

        // Then
        assertTrue(responses.isEmpty());

        verify(stateService).getEliminatedState();
        verify(productCategoryRepository).findAll();
    }

    @Test
    @DisplayName("Should handle repository with only eliminated categories")
    void shouldHandleRepositoryWithOnlyEliminatedCategories() {
        // Given
        ProductCategory eliminatedCategory1 = TestDataFactory.createValidProductCategory(eliminatedState);
        eliminatedCategory1.setId(1L);
        eliminatedCategory1.setCategory("Eliminated 1");
        
        ProductCategory eliminatedCategory2 = TestDataFactory.createValidProductCategory(eliminatedState);
        eliminatedCategory2.setId(2L);
        eliminatedCategory2.setCategory("Eliminated 2");

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(productCategoryRepository.findAll()).thenReturn(Arrays.asList(eliminatedCategory1, eliminatedCategory2));

        // When
        List<ProductCategoryResponse> responses = productCategoryService.getAllProductCategories();

        // Then
        assertTrue(responses.isEmpty());

        verify(stateService).getEliminatedState();
        verify(productCategoryRepository).findAll();
    }

    @Test
    @DisplayName("Should handle categories with null states gracefully")
    void shouldHandleCategoriesWithNullStatesGracefully() {
        // Given
        ProductCategory categoryWithNullState = new ProductCategory();
        categoryWithNullState.setId(1L);
        categoryWithNullState.setCategory("Category with null state");
        categoryWithNullState.setState(null);

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(productCategoryRepository.findAll()).thenReturn(Arrays.asList(categoryWithNullState, productCategory));

        // When
        List<ProductCategoryResponse> responses = productCategoryService.getAllProductCategories();

        // Then
        assertEquals(1, responses.size()); // Only the category with valid state
        assertEquals(productCategory.getCategory(), responses.get(0).category());

        verify(stateService).getEliminatedState();
        verify(productCategoryRepository).findAll();
    }
}