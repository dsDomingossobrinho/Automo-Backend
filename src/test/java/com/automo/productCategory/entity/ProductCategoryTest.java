package com.automo.productCategory.entity;

import com.automo.productCategory.entity.ProductCategory;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for ProductCategory Entity")
class ProductCategoryTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid ProductCategory entity")
    void shouldCreateValidProductCategoryEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        ProductCategory productCategory = TestDataFactory.createValidProductCategory(state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Electronics", productCategory.getCategory());
        assertEquals("Electronic devices and accessories", productCategory.getDescription());
        assertEquals(state, productCategory.getState());
    }

    @Test
    @DisplayName("Should fail validation with null category")
    void shouldFailValidationWithNullCategory() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(null);
        productCategory.setDescription("Test description");
        productCategory.setState(state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("category")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Category is required")));
    }

    @Test
    @DisplayName("Should fail validation with blank category")
    void shouldFailValidationWithBlankCategory() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory("");
        productCategory.setDescription("Test description");
        productCategory.setState(state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("category")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Category is required")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace only category")
    void shouldFailValidationWithWhitespaceOnlyCategory() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory("   ");
        productCategory.setDescription("Test description");
        productCategory.setState(state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("category")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory("Test Category");
        productCategory.setDescription("Test description");
        productCategory.setState(null);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("State is required")));
    }

    @Test
    @DisplayName("Should create ProductCategory with null description")
    void shouldCreateProductCategoryWithNullDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory("Simple Category");
        productCategory.setDescription(null);
        productCategory.setState(state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Simple Category", productCategory.getCategory());
        assertNull(productCategory.getDescription());
        assertEquals(state, productCategory.getState());
    }

    @Test
    @DisplayName("Should create ProductCategory with empty description")
    void shouldCreateProductCategoryWithEmptyDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory("Category with Empty Desc");
        productCategory.setDescription("");
        productCategory.setState(state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Category with Empty Desc", productCategory.getCategory());
        assertEquals("", productCategory.getDescription());
    }

    @Test
    @DisplayName("Should handle different states")
    void shouldHandleDifferentStates() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        State eliminatedState = TestDataFactory.createEliminatedState();
        
        ProductCategory activeCategory = TestDataFactory.createValidProductCategory(activeState);
        ProductCategory inactiveCategory = TestDataFactory.createValidProductCategory(inactiveState);
        inactiveCategory.setCategory("Inactive Category");
        ProductCategory eliminatedCategory = TestDataFactory.createValidProductCategory(eliminatedState);
        eliminatedCategory.setCategory("Eliminated Category");
        
        // When
        Set<ConstraintViolation<ProductCategory>> activeViolations = validator.validate(activeCategory);
        Set<ConstraintViolation<ProductCategory>> inactiveViolations = validator.validate(inactiveCategory);
        Set<ConstraintViolation<ProductCategory>> eliminatedViolations = validator.validate(eliminatedCategory);
        
        // Then
        assertTrue(activeViolations.isEmpty());
        assertTrue(inactiveViolations.isEmpty());
        assertTrue(eliminatedViolations.isEmpty());
        assertEquals("ACTIVE", activeCategory.getState().getState());
        assertEquals("INACTIVE", inactiveCategory.getState().getState());
        assertEquals("ELIMINATED", eliminatedCategory.getState().getState());
    }

    @Test
    @DisplayName("Should handle long category names")
    void shouldHandleLongCategoryNames() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        String longCategoryName = "A".repeat(255);
        String longDescription = "B".repeat(1000);
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(longCategoryName);
        productCategory.setDescription(longDescription);
        productCategory.setState(state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longCategoryName, productCategory.getCategory());
        assertEquals(longDescription, productCategory.getDescription());
    }

    @Test
    @DisplayName("Should handle special characters in category and description")
    void shouldHandleSpecialCharactersInCategoryAndDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory("Category & Co. - Special €100");
        productCategory.setDescription("Description with special chars: áéíóú, çñü, @#$%");
        productCategory.setState(state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Category & Co. - Special €100", productCategory.getCategory());
        assertEquals("Description with special chars: áéíóú, çñü, @#$%", productCategory.getDescription());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        ProductCategory category1 = TestDataFactory.createValidProductCategory(state);
        ProductCategory category2 = TestDataFactory.createValidProductCategory(state);
        category1.setId(1L);
        category2.setId(1L);
        
        // Then
        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
        
        // When different IDs
        category2.setId(2L);
        
        // Then
        assertNotEquals(category1, category2);
    }

    @Test
    @DisplayName("Should create ProductCategory with all valid variations")
    void shouldCreateProductCategoryWithAllValidVariations() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        ProductCategory category1 = new ProductCategory("Electronics", "Electronic devices", state);
        ProductCategory category2 = new ProductCategory("Books", null, state);
        ProductCategory category3 = new ProductCategory("Home & Garden", "", state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations1 = validator.validate(category1);
        Set<ConstraintViolation<ProductCategory>> violations2 = validator.validate(category2);
        Set<ConstraintViolation<ProductCategory>> violations3 = validator.validate(category3);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertTrue(violations3.isEmpty());
        
        assertEquals("Electronics", category1.getCategory());
        assertEquals("Electronic devices", category1.getDescription());
        
        assertEquals("Books", category2.getCategory());
        assertNull(category2.getDescription());
        
        assertEquals("Home & Garden", category3.getCategory());
        assertEquals("", category3.getDescription());
    }

    @Test
    @DisplayName("Should handle numeric and mixed characters in category")
    void shouldHandleNumericAndMixedCharactersInCategory() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory("Category 123 - Version 2.0");
        productCategory.setDescription("123 ABC def 456");
        productCategory.setState(state);
        
        // When
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Category 123 - Version 2.0", productCategory.getCategory());
        assertEquals("123 ABC def 456", productCategory.getDescription());
    }

    @Test
    @DisplayName("Should maintain AbstractModel inheritance properties")
    void shouldMaintainAbstractModelInheritanceProperties() {
        // Given
        State state = TestDataFactory.createActiveState();
        ProductCategory productCategory = TestDataFactory.createValidProductCategory(state);
        productCategory.setId(99L);
        
        // When & Then
        assertEquals(99L, productCategory.getId());
        assertNull(productCategory.getCreatedAt()); // Will be set by JPA
        assertNull(productCategory.getUpdatedAt()); // Will be set by JPA
        assertEquals(state, productCategory.getState());
    }
}