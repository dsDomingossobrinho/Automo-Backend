package com.automo.productCategory.repository;

import com.automo.productCategory.entity.ProductCategory;
import com.automo.productCategory.response.ProductCategoryResponse;
import com.automo.state.entity.State;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for ProductCategoryRepository")
class ProductCategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private ProductCategory productCategory1;
    private ProductCategory productCategory2;
    private ProductCategory eliminatedCategory;

    @BeforeEach
    void setUp() {
        // Create and persist states
        activeState = TestDataFactory.createActiveState();
        inactiveState = TestDataFactory.createInactiveState();
        eliminatedState = TestDataFactory.createEliminatedState();
        
        activeState = entityManager.persistAndFlush(activeState);
        inactiveState = entityManager.persistAndFlush(inactiveState);
        eliminatedState = entityManager.persistAndFlush(eliminatedState);

        // Create and persist product categories
        productCategory1 = TestDataFactory.createValidProductCategory(activeState);
        productCategory1.setCategory("Electronics");
        productCategory1.setDescription("Electronic devices and accessories");
        productCategory1 = entityManager.persistAndFlush(productCategory1);

        productCategory2 = TestDataFactory.createValidProductCategory(inactiveState);
        productCategory2.setCategory("Books");
        productCategory2.setDescription("Books and magazines");
        productCategory2 = entityManager.persistAndFlush(productCategory2);

        eliminatedCategory = TestDataFactory.createValidProductCategory(eliminatedState);
        eliminatedCategory.setCategory("Eliminated Category");
        eliminatedCategory.setDescription("This category is eliminated");
        eliminatedCategory = entityManager.persistAndFlush(eliminatedCategory);

        entityManager.clear();
    }

    @Test
    @DisplayName("Should find product category by category name")
    void shouldFindProductCategoryByCategoryName() {
        // When
        Optional<ProductCategory> result = productCategoryRepository.findByCategory("Electronics");

        // Then
        assertTrue(result.isPresent());
        ProductCategory found = result.get();
        assertEquals("Electronics", found.getCategory());
        assertEquals("Electronic devices and accessories", found.getDescription());
        assertEquals(activeState.getId(), found.getState().getId());
    }

    @Test
    @DisplayName("Should return empty when category name not found")
    void shouldReturnEmptyWhenCategoryNameNotFound() {
        // When
        Optional<ProductCategory> result = productCategoryRepository.findByCategory("Non-existent Category");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find product categories by state ID")
    void shouldFindProductCategoriesByStateId() {
        // When
        List<ProductCategory> activeCategories = productCategoryRepository.findByStateId(activeState.getId());
        List<ProductCategory> inactiveCategories = productCategoryRepository.findByStateId(inactiveState.getId());
        List<ProductCategory> eliminatedCategories = productCategoryRepository.findByStateId(eliminatedState.getId());

        // Then
        assertEquals(1, activeCategories.size());
        assertEquals("Electronics", activeCategories.get(0).getCategory());

        assertEquals(1, inactiveCategories.size());
        assertEquals("Books", inactiveCategories.get(0).getCategory());

        assertEquals(1, eliminatedCategories.size());
        assertEquals("Eliminated Category", eliminatedCategories.get(0).getCategory());
    }

    @Test
    @DisplayName("Should return empty list when no categories found for state")
    void shouldReturnEmptyListWhenNoCategoriesFoundForState() {
        // Given
        Long nonExistentStateId = 999L;

        // When
        List<ProductCategory> categories = productCategoryRepository.findByStateId(nonExistentStateId);

        // Then
        assertTrue(categories.isEmpty());
    }

    @Test
    @DisplayName("Should check if category exists by name")
    void shouldCheckIfCategoryExistsByName() {
        // When & Then
        assertTrue(productCategoryRepository.existsByCategory("Electronics"));
        assertTrue(productCategoryRepository.existsByCategory("Books"));
        assertTrue(productCategoryRepository.existsByCategory("Eliminated Category"));
        assertFalse(productCategoryRepository.existsByCategory("Non-existent Category"));
    }

    @Test
    @DisplayName("Should find all categories as responses")
    void shouldFindAllCategoriesAsResponses() {
        // When
        List<ProductCategoryResponse> responses = productCategoryRepository.findAllResponse();

        // Then
        assertEquals(3, responses.size());
        
        // Verify first category
        ProductCategoryResponse electronicsResponse = responses.stream()
            .filter(r -> r.category().equals("Electronics"))
            .findFirst()
            .orElseThrow();
        
        assertEquals("Electronics", electronicsResponse.category());
        assertEquals("Electronic devices and accessories", electronicsResponse.description());
        assertEquals(activeState.getId(), electronicsResponse.stateId());
        assertEquals("ACTIVE", electronicsResponse.stateName());
        assertNotNull(electronicsResponse.createdAt());
        assertNotNull(electronicsResponse.updatedAt());

        // Verify second category
        ProductCategoryResponse booksResponse = responses.stream()
            .filter(r -> r.category().equals("Books"))
            .findFirst()
            .orElseThrow();
        
        assertEquals("Books", booksResponse.category());
        assertEquals("Books and magazines", booksResponse.description());
        assertEquals(inactiveState.getId(), booksResponse.stateId());
        assertEquals("INACTIVE", booksResponse.stateName());
    }

    @Test
    @DisplayName("Should find category response by ID")
    void shouldFindCategoryResponseById() {
        // When
        Optional<ProductCategoryResponse> result = productCategoryRepository.findResponseById(productCategory1.getId());

        // Then
        assertTrue(result.isPresent());
        ProductCategoryResponse response = result.get();
        assertEquals(productCategory1.getId(), response.id());
        assertEquals("Electronics", response.category());
        assertEquals("Electronic devices and accessories", response.description());
        assertEquals(activeState.getId(), response.stateId());
        assertEquals("ACTIVE", response.stateName());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
    }

    @Test
    @DisplayName("Should return empty when finding response by non-existent ID")
    void shouldReturnEmptyWhenFindingResponseByNonExistentId() {
        // When
        Optional<ProductCategoryResponse> result = productCategoryRepository.findResponseById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should save new product category")
    void shouldSaveNewProductCategory() {
        // Given
        ProductCategory newCategory = new ProductCategory();
        newCategory.setCategory("Home & Garden");
        newCategory.setDescription("Home improvement and garden items");
        newCategory.setState(activeState);

        // When
        ProductCategory saved = productCategoryRepository.save(newCategory);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Home & Garden", saved.getCategory());
        assertEquals("Home improvement and garden items", saved.getDescription());
        assertEquals(activeState.getId(), saved.getState().getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());

        // Verify it can be found
        Optional<ProductCategory> found = productCategoryRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Home & Garden", found.get().getCategory());
    }

    @Test
    @DisplayName("Should update existing product category")
    void shouldUpdateExistingProductCategory() {
        // Given
        ProductCategory toUpdate = productCategoryRepository.findById(productCategory1.getId()).orElseThrow();
        String originalUpdatedAt = toUpdate.getUpdatedAt() != null ? toUpdate.getUpdatedAt().toString() : null;

        // When
        toUpdate.setCategory("Updated Electronics");
        toUpdate.setDescription("Updated description for electronics");
        ProductCategory updated = productCategoryRepository.save(toUpdate);

        // Then
        assertEquals("Updated Electronics", updated.getCategory());
        assertEquals("Updated description for electronics", updated.getDescription());
        
        // Verify the update was persisted
        ProductCategory reloaded = productCategoryRepository.findById(productCategory1.getId()).orElseThrow();
        assertEquals("Updated Electronics", reloaded.getCategory());
        assertEquals("Updated description for electronics", reloaded.getDescription());
    }

    @Test
    @DisplayName("Should delete product category")
    void shouldDeleteProductCategory() {
        // Given
        Long categoryId = productCategory1.getId();
        assertTrue(productCategoryRepository.existsById(categoryId));

        // When
        productCategoryRepository.deleteById(categoryId);

        // Then
        assertFalse(productCategoryRepository.existsById(categoryId));
        Optional<ProductCategory> deleted = productCategoryRepository.findById(categoryId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should find all categories including different states")
    void shouldFindAllCategoriesIncludingDifferentStates() {
        // When
        List<ProductCategory> allCategories = productCategoryRepository.findAll();

        // Then
        assertEquals(3, allCategories.size());
        
        // Verify all categories are present
        assertTrue(allCategories.stream().anyMatch(c -> c.getCategory().equals("Electronics")));
        assertTrue(allCategories.stream().anyMatch(c -> c.getCategory().equals("Books")));
        assertTrue(allCategories.stream().anyMatch(c -> c.getCategory().equals("Eliminated Category")));
    }

    @Test
    @DisplayName("Should handle category name case sensitivity")
    void shouldHandleCategoryNameCaseSensitivity() {
        // When & Then
        assertTrue(productCategoryRepository.existsByCategory("Electronics"));
        assertFalse(productCategoryRepository.existsByCategory("electronics"));
        assertFalse(productCategoryRepository.existsByCategory("ELECTRONICS"));
        
        Optional<ProductCategory> exact = productCategoryRepository.findByCategory("Electronics");
        Optional<ProductCategory> lowercase = productCategoryRepository.findByCategory("electronics");
        Optional<ProductCategory> uppercase = productCategoryRepository.findByCategory("ELECTRONICS");
        
        assertTrue(exact.isPresent());
        assertFalse(lowercase.isPresent());
        assertFalse(uppercase.isPresent());
    }

    @Test
    @DisplayName("Should handle null and empty category names gracefully")
    void shouldHandleNullAndEmptyCategoryNamesGracefully() {
        // When & Then
        assertFalse(productCategoryRepository.existsByCategory(null));
        assertFalse(productCategoryRepository.existsByCategory(""));
        assertFalse(productCategoryRepository.existsByCategory("   "));
        
        Optional<ProductCategory> nullResult = productCategoryRepository.findByCategory(null);
        Optional<ProductCategory> emptyResult = productCategoryRepository.findByCategory("");
        Optional<ProductCategory> spacesResult = productCategoryRepository.findByCategory("   ");
        
        assertFalse(nullResult.isPresent());
        assertFalse(emptyResult.isPresent());
        assertFalse(spacesResult.isPresent());
    }

    @Test
    @DisplayName("Should save category with null description")
    void shouldSaveCategoryWithNullDescription() {
        // Given
        ProductCategory categoryWithNullDesc = new ProductCategory();
        categoryWithNullDesc.setCategory("Category No Description");
        categoryWithNullDesc.setDescription(null);
        categoryWithNullDesc.setState(activeState);

        // When
        ProductCategory saved = productCategoryRepository.save(categoryWithNullDesc);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Category No Description", saved.getCategory());
        assertNull(saved.getDescription());
        assertEquals(activeState.getId(), saved.getState().getId());
    }

    @Test
    @DisplayName("Should count categories correctly")
    void shouldCountCategoriesCorrectly() {
        // When
        long totalCount = productCategoryRepository.count();

        // Then
        assertEquals(3, totalCount);
    }

    @Test
    @DisplayName("Should handle concurrent modifications")
    void shouldHandleConcurrentModifications() {
        // Given
        ProductCategory category = productCategoryRepository.findById(productCategory1.getId()).orElseThrow();
        
        // When - Simulate concurrent modification by updating the same entity
        category.setCategory("Modified by First Thread");
        ProductCategory firstUpdate = productCategoryRepository.save(category);
        
        // Reload and modify again
        ProductCategory reloaded = productCategoryRepository.findById(productCategory1.getId()).orElseThrow();
        reloaded.setCategory("Modified by Second Thread");
        ProductCategory secondUpdate = productCategoryRepository.save(reloaded);

        // Then
        assertEquals("Modified by Second Thread", secondUpdate.getCategory());
        
        // Verify final state
        ProductCategory finalState = productCategoryRepository.findById(productCategory1.getId()).orElseThrow();
        assertEquals("Modified by Second Thread", finalState.getCategory());
    }
}