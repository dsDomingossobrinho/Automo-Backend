package com.automo.product.repository;

import com.automo.product.entity.Product;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for ProductRepository")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState = entityManager.persistAndFlush(activeState);

        inactiveState = TestDataFactory.createInactiveState();
        inactiveState = entityManager.persistAndFlush(inactiveState);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = entityManager.persistAndFlush(eliminatedState);

        testProduct = TestDataFactory.createValidProduct(activeState);
        testProduct = entityManager.persistAndFlush(testProduct);

        entityManager.clear();
    }

    @Test
    @DisplayName("Should find product by id successfully")
    void shouldFindProductByIdSuccessfully() {
        Optional<Product> found = productRepository.findById(testProduct.getId());

        assertTrue(found.isPresent());
        assertEquals(testProduct.getName(), found.get().getName());
        assertEquals(testProduct.getDescription(), found.get().getDescription());
        assertEquals(testProduct.getPrice(), found.get().getPrice());
        assertEquals(testProduct.getImg(), found.get().getImg());
        assertEquals(testProduct.getState().getId(), found.get().getState().getId());
    }

    @Test
    @DisplayName("Should find products by state id")
    void shouldFindProductsByStateId() {
        // Create additional products
        Product activeProduct1 = TestDataFactory.createValidProduct(activeState);
        activeProduct1.setName("Active Product 1");
        activeProduct1.setPrice(new BigDecimal("100.00"));
        entityManager.persistAndFlush(activeProduct1);

        Product activeProduct2 = TestDataFactory.createValidProduct(activeState);
        activeProduct2.setName("Active Product 2");
        activeProduct2.setPrice(new BigDecimal("200.00"));
        entityManager.persistAndFlush(activeProduct2);

        Product inactiveProduct = TestDataFactory.createValidProduct(inactiveState);
        inactiveProduct.setName("Inactive Product");
        inactiveProduct.setPrice(new BigDecimal("300.00"));
        entityManager.persistAndFlush(inactiveProduct);

        entityManager.clear();

        List<Product> activeProducts = productRepository.findByStateId(activeState.getId());
        List<Product> inactiveProducts = productRepository.findByStateId(inactiveState.getId());

        assertEquals(3, activeProducts.size()); // testProduct + 2 new active products
        assertEquals(1, inactiveProducts.size());

        assertTrue(activeProducts.stream().anyMatch(p -> p.getName().equals("BMW X5")));
        assertTrue(activeProducts.stream().anyMatch(p -> p.getName().equals("Active Product 1")));
        assertTrue(activeProducts.stream().anyMatch(p -> p.getName().equals("Active Product 2")));
        assertTrue(inactiveProducts.stream().anyMatch(p -> p.getName().equals("Inactive Product")));
    }

    @Test
    @DisplayName("Should save product successfully")
    void shouldSaveProductSuccessfully() {
        Product newProduct = new Product();
        newProduct.setName("New Test Product");
        newProduct.setDescription("New product for testing");
        newProduct.setImg("new-test-product.jpg");
        newProduct.setPrice(new BigDecimal("999.99"));
        newProduct.setState(activeState);

        Product savedProduct = productRepository.save(newProduct);

        assertNotNull(savedProduct.getId());
        assertEquals("New Test Product", savedProduct.getName());
        assertEquals("New product for testing", savedProduct.getDescription());
        assertEquals("new-test-product.jpg", savedProduct.getImg());
        assertEquals(new BigDecimal("999.99"), savedProduct.getPrice());
        assertEquals(activeState.getId(), savedProduct.getState().getId());
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        testProduct.setName("Updated Product Name");
        testProduct.setDescription("Updated description");
        testProduct.setPrice(new BigDecimal("1299.99"));
        testProduct.setImg("updated-image.jpg");

        Product updatedProduct = productRepository.save(testProduct);

        assertEquals("Updated Product Name", updatedProduct.getName());
        assertEquals("Updated description", updatedProduct.getDescription());
        assertEquals(new BigDecimal("1299.99"), updatedProduct.getPrice());
        assertEquals("updated-image.jpg", updatedProduct.getImg());
        assertEquals(testProduct.getId(), updatedProduct.getId());
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {
        Long productId = testProduct.getId();

        productRepository.delete(testProduct);
        entityManager.flush();

        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    @DisplayName("Should find all products")
    void shouldFindAllProducts() {
        // Create additional products
        Product product2 = TestDataFactory.createValidProduct(activeState);
        product2.setName("Product Two");
        product2.setPrice(new BigDecimal("222.22"));
        entityManager.persistAndFlush(product2);

        Product product3 = TestDataFactory.createValidProduct(inactiveState);
        product3.setName("Product Three");
        product3.setPrice(new BigDecimal("333.33"));
        entityManager.persistAndFlush(product3);

        entityManager.clear();

        List<Product> allProducts = productRepository.findAll();

        assertEquals(3, allProducts.size());
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("BMW X5")));
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Product Two")));
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Product Three")));
    }

    @Test
    @DisplayName("Should return empty list when no products exist for state")
    void shouldReturnEmptyListWhenNoProductsExistForState() {
        List<Product> products = productRepository.findByStateId(eliminatedState.getId());

        assertTrue(products.isEmpty());
    }

    @Test
    @DisplayName("Should handle products with null image")
    void shouldHandleProductsWithNullImage() {
        Product productWithoutImage = new Product();
        productWithoutImage.setName("Product Without Image");
        productWithoutImage.setDescription("Product with no image");
        productWithoutImage.setImg(null);
        productWithoutImage.setPrice(new BigDecimal("49.99"));
        productWithoutImage.setState(activeState);

        Product savedProduct = productRepository.save(productWithoutImage);

        assertNotNull(savedProduct.getId());
        assertEquals("Product Without Image", savedProduct.getName());
        assertNull(savedProduct.getImg());
        assertEquals(new BigDecimal("49.99"), savedProduct.getPrice());
    }

    @Test
    @DisplayName("Should handle products with null description")
    void shouldHandleProductsWithNullDescription() {
        Product productWithoutDescription = new Product();
        productWithoutDescription.setName("Product Without Description");
        productWithoutDescription.setDescription(null);
        productWithoutDescription.setImg("no-desc-product.jpg");
        productWithoutDescription.setPrice(new BigDecimal("79.99"));
        productWithoutDescription.setState(activeState);

        Product savedProduct = productRepository.save(productWithoutDescription);

        assertNotNull(savedProduct.getId());
        assertEquals("Product Without Description", savedProduct.getName());
        assertNull(savedProduct.getDescription());
        assertEquals("no-desc-product.jpg", savedProduct.getImg());
        assertEquals(new BigDecimal("79.99"), savedProduct.getPrice());
    }

    @Test
    @DisplayName("Should handle products with different states")
    void shouldHandleProductsWithDifferentStates() {
        Product activeProduct = TestDataFactory.createValidProduct(activeState);
        activeProduct.setName("Active Product");
        activeProduct = entityManager.persistAndFlush(activeProduct);

        Product inactiveProduct = TestDataFactory.createValidProduct(inactiveState);
        inactiveProduct.setName("Inactive Product");
        inactiveProduct = entityManager.persistAndFlush(inactiveProduct);

        Product eliminatedProduct = TestDataFactory.createValidProduct(eliminatedState);
        eliminatedProduct.setName("Eliminated Product");
        eliminatedProduct = entityManager.persistAndFlush(eliminatedProduct);

        entityManager.clear();

        Optional<Product> foundActive = productRepository.findById(activeProduct.getId());
        Optional<Product> foundInactive = productRepository.findById(inactiveProduct.getId());
        Optional<Product> foundEliminated = productRepository.findById(eliminatedProduct.getId());

        assertTrue(foundActive.isPresent());
        assertTrue(foundInactive.isPresent());
        assertTrue(foundEliminated.isPresent());
        
        assertEquals("ACTIVE", foundActive.get().getState().getState());
        assertEquals("INACTIVE", foundInactive.get().getState().getState());
        assertEquals("ELIMINATED", foundEliminated.get().getState().getState());
    }

    @Test
    @DisplayName("Should handle products with very large prices")
    void shouldHandleProductsWithVeryLargePrices() {
        Product expensiveProduct = new Product();
        expensiveProduct.setName("Expensive Product");
        expensiveProduct.setDescription("Very expensive luxury item");
        expensiveProduct.setImg("expensive.jpg");
        expensiveProduct.setPrice(new BigDecimal("999999999.99"));
        expensiveProduct.setState(activeState);

        Product savedProduct = productRepository.save(expensiveProduct);

        assertNotNull(savedProduct.getId());
        assertEquals(new BigDecimal("999999999.99"), savedProduct.getPrice());
    }

    @Test
    @DisplayName("Should handle products with minimal prices")
    void shouldHandleProductsWithMinimalPrices() {
        Product cheapProduct = new Product();
        cheapProduct.setName("Very Cheap Product");
        cheapProduct.setDescription("Almost free item");
        cheapProduct.setImg("cheap.jpg");
        cheapProduct.setPrice(new BigDecimal("0.01"));
        cheapProduct.setState(activeState);

        Product savedProduct = productRepository.save(cheapProduct);

        assertNotNull(savedProduct.getId());
        assertEquals(new BigDecimal("0.01"), savedProduct.getPrice());
    }

    @Test
    @DisplayName("Should persist and retrieve timestamps correctly")
    void shouldPersistAndRetrieveTimestampsCorrectly() {
        Optional<Product> foundProduct = productRepository.findById(testProduct.getId());

        assertTrue(foundProduct.isPresent());
        Product product = foundProduct.get();
        
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
        
        // Update the product to test updatedAt
        product.setName("Updated Name for Timestamp Test");
        Product savedProduct = productRepository.save(product);
        
        assertNotNull(savedProduct.getUpdatedAt());
        assertTrue(savedProduct.getUpdatedAt().isAfter(savedProduct.getCreatedAt()) || 
                   savedProduct.getUpdatedAt().isEqual(savedProduct.getCreatedAt()));
    }

    @Test
    @DisplayName("Should handle special characters in product name and description")
    void shouldHandleSpecialCharactersInProductNameAndDescription() {
        Product specialProduct = new Product();
        specialProduct.setName("Product with Special Chars: áéíóú ñç €$£");
        specialProduct.setDescription("Description with symbols: @#$%&*()_+{}[]|\\:;\"'<>?,./");
        specialProduct.setImg("special-chars.jpg");
        specialProduct.setPrice(new BigDecimal("123.45"));
        specialProduct.setState(activeState);

        Product savedProduct = productRepository.save(specialProduct);

        assertNotNull(savedProduct.getId());
        assertEquals("Product with Special Chars: áéíóú ñç €$£", savedProduct.getName());
        assertEquals("Description with symbols: @#$%&*()_+{}[]|\\:;\"'<>?,./", savedProduct.getDescription());
    }
}