package com.automo.product.entity;

import com.automo.product.entity.Product;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Product Entity")
class ProductTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Product entity")
    void shouldCreateValidProductEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        Product product = TestDataFactory.createValidProduct(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("BMW X5", product.getName());
        assertEquals("Luxury SUV", product.getDescription());
        assertEquals(new BigDecimal("50000.00"), product.getPrice());
        assertEquals("bmw-x5.jpg", product.getImg());
        assertEquals(state, product.getState());
    }

    @Test
    @DisplayName("Should fail validation with null name")
    void shouldFailValidationWithNullName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product = new Product();
        product.setName(null);
        product.setDescription("Test description");
        product.setPrice(new BigDecimal("100.00"));
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with blank name")
    void shouldFailValidationWithBlankName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product = new Product();
        product.setName("");
        product.setDescription("Test description");
        product.setPrice(new BigDecimal("100.00"));
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with null price")
    void shouldFailValidationWithNullPrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test description");
        product.setPrice(null);
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation with negative price")
    void shouldFailValidationWithNegativePrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test description");
        product.setPrice(new BigDecimal("-100.00"));
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation with zero price")
    void shouldFailValidationWithZeroPrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test description");
        product.setPrice(BigDecimal.ZERO);
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test description");
        product.setPrice(new BigDecimal("100.00"));
        product.setState(null);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should create product with null description and image")
    void shouldCreateProductWithNullDescriptionAndImage() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product = new Product();
        product.setName("Simple Product");
        product.setDescription(null);
        product.setImg(null);
        product.setPrice(new BigDecimal("99.99"));
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Simple Product", product.getName());
        assertNull(product.getDescription());
        assertNull(product.getImg());
        assertEquals(new BigDecimal("99.99"), product.getPrice());
    }

    @Test
    @DisplayName("Should create product with very large price")
    void shouldCreateProductWithVeryLargePrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product = new Product();
        product.setName("Expensive Product");
        product.setDescription("Very expensive item");
        product.setImg("expensive.jpg");
        product.setPrice(new BigDecimal("999999999.99"));
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("999999999.99"), product.getPrice());
    }

    @Test
    @DisplayName("Should create product with minimal valid price")
    void shouldCreateProductWithMinimalValidPrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product = new Product();
        product.setName("Cheap Product");
        product.setDescription("Very cheap item");
        product.setImg("cheap.jpg");
        product.setPrice(new BigDecimal("0.01"));
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("0.01"), product.getPrice());
    }

    @Test
    @DisplayName("Should handle different states")
    void shouldHandleDifferentStates() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        
        Product activeProduct = TestDataFactory.createValidProduct(activeState);
        Product inactiveProduct = TestDataFactory.createValidProduct(inactiveState);
        inactiveProduct.setName("Inactive Product");
        
        // When
        Set<ConstraintViolation<Product>> activeViolations = validator.validate(activeProduct);
        Set<ConstraintViolation<Product>> inactiveViolations = validator.validate(inactiveProduct);
        
        // Then
        assertTrue(activeViolations.isEmpty());
        assertTrue(inactiveViolations.isEmpty());
        assertEquals("ACTIVE", activeProduct.getState().getState());
        assertEquals("INACTIVE", inactiveProduct.getState().getState());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product1 = TestDataFactory.createValidProduct(state);
        Product product2 = TestDataFactory.createValidProduct(state);
        product1.setId(1L);
        product2.setId(1L);
        
        // Then
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
        
        // When different IDs
        product2.setId(2L);
        
        // Then
        assertNotEquals(product1, product2);
    }

    @Test
    @DisplayName("Should handle long product names and descriptions")
    void shouldHandleLongProductNamesAndDescriptions() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        String longName = "A".repeat(255);
        String longDescription = "B".repeat(1000);
        
        Product product = new Product();
        product.setName(longName);
        product.setDescription(longDescription);
        product.setImg("product.jpg");
        product.setPrice(new BigDecimal("100.00"));
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longName, product.getName());
        assertEquals(longDescription, product.getDescription());
    }

    @Test
    @DisplayName("Should handle special characters in name and description")
    void shouldHandleSpecialCharactersInNameAndDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Product product = new Product();
        product.setName("Product & Co. - Special €100");
        product.setDescription("Description with special chars: áéíóú, çñü, @#$%");
        product.setImg("special-product.jpg");
        product.setPrice(new BigDecimal("123.45"));
        product.setState(state);
        
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Product & Co. - Special €100", product.getName());
        assertEquals("Description with special chars: áéíóú, çñü, @#$%", product.getDescription());
    }
}