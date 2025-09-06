package com.automo.product.service;

import com.automo.payment.service.FileStorageService;
import com.automo.product.dto.ProductDto;
import com.automo.product.entity.Product;
import com.automo.product.repository.ProductRepository;
import com.automo.product.response.ProductResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for ProductServiceImpl")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StateService stateService;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private State activeState;
    private State eliminatedState;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        testProduct = TestDataFactory.createValidProduct(activeState);
        testProduct.setId(1L);
        
        productDto = new ProductDto(
            "New Product", 
            "new-product.jpg", 
            "New product description", 
            new BigDecimal("199.99"), 
            1L
        );
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        // Given
        when(stateService.findById(1L)).thenReturn(activeState);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponse result = productService.createProduct(productDto);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.id());
        assertEquals(testProduct.getName(), result.name());
        assertEquals(testProduct.getPrice(), result.price());
        
        verify(stateService).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponse result = productService.updateProduct(productId, productDto);

        // Then
        assertNotNull(result);
        verify(productRepository).findById(productId);
        verify(stateService).findById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing product")
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> productService.updateProduct(productId, productDto));
        
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should get all products excluding eliminated")
    void shouldGetAllProductsExcludingEliminated() {
        // Given
        Product product1 = TestDataFactory.createValidProduct(activeState);
        product1.setId(1L);
        product1.setName("Product 1");
        
        Product product2 = TestDataFactory.createValidProduct(activeState);
        product2.setId(2L);
        product2.setName("Product 2");
        
        Product eliminatedProduct = TestDataFactory.createValidProduct(eliminatedState);
        eliminatedProduct.setId(3L);
        eliminatedProduct.setName("Eliminated Product");
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, eliminatedProduct));

        // When
        List<ProductResponse> result = productService.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.name().equals("Product 1")));
        assertTrue(result.stream().anyMatch(p -> p.name().equals("Product 2")));
        assertTrue(result.stream().noneMatch(p -> p.name().equals("Eliminated Product")));
        
        verify(stateService).getEliminatedState();
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should get product by id successfully")
    void shouldGetProductByIdSuccessfully() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existing product")
    void shouldThrowExceptionWhenGettingNonExistingProduct() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> productService.getProductById(productId));
        
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should get product by id response successfully")
    void shouldGetProductByIdResponseSuccessfully() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        ProductResponse result = productService.getProductByIdResponse(productId);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.id());
        assertEquals(testProduct.getName(), result.name());
        
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should get products by state successfully")
    void shouldGetProductsByStateSuccessfully() {
        // Given
        Long stateId = 1L;
        List<Product> products = Arrays.asList(testProduct);
        
        when(productRepository.findByStateId(stateId)).thenReturn(products);

        // When
        List<ProductResponse> result = productService.getProductsByState(stateId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct.getName(), result.get(0).name());
        
        verify(productRepository).findByStateId(stateId);
    }

    @Test
    @DisplayName("Should soft delete product successfully")
    void shouldSoftDeleteProductSuccessfully() {
        // Given
        Long productId = 1L;
        testProduct.setImg("product-image.jpg");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        doNothing().when(fileStorageService).deleteProductImage(anyString());

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productRepository).findById(productId);
        verify(stateService).getEliminatedState();
        verify(fileStorageService).deleteProductImage("product-image.jpg");
        verify(productRepository).save(testProduct);
        assertEquals(eliminatedState, testProduct.getState());
    }

    @Test
    @DisplayName("Should soft delete product without image successfully")
    void shouldSoftDeleteProductWithoutImageSuccessfully() {
        // Given
        Long productId = 1L;
        testProduct.setImg(null);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productRepository).findById(productId);
        verify(stateService).getEliminatedState();
        verify(fileStorageService, never()).deleteProductImage(anyString());
        verify(productRepository).save(testProduct);
        assertEquals(eliminatedState, testProduct.getState());
    }

    @Test
    @DisplayName("Should upload product image successfully")
    void shouldUploadProductImageSuccessfully() {
        // Given
        Long productId = 1L;
        MultipartFile imageFile = new MockMultipartFile(
            "image", "test-image.jpg", "image/jpeg", "test image content".getBytes()
        );
        String newFilename = "new-product-image.jpg";
        
        testProduct.setImg("old-image.jpg");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(fileStorageService.storeProductImage(imageFile)).thenReturn(newFilename);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        doNothing().when(fileStorageService).deleteProductImage(anyString());

        // When
        ProductResponse result = productService.uploadProductImage(productId, imageFile);

        // Then
        assertNotNull(result);
        verify(productRepository).findById(productId);
        verify(fileStorageService).deleteProductImage("old-image.jpg");
        verify(fileStorageService).storeProductImage(imageFile);
        verify(productRepository).save(testProduct);
        assertEquals(newFilename, testProduct.getImg());
    }

    @Test
    @DisplayName("Should upload product image without deleting old image when none exists")
    void shouldUploadProductImageWithoutDeletingOldImageWhenNoneExists() {
        // Given
        Long productId = 1L;
        MultipartFile imageFile = new MockMultipartFile(
            "image", "test-image.jpg", "image/jpeg", "test image content".getBytes()
        );
        String newFilename = "new-product-image.jpg";
        
        testProduct.setImg(null);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(fileStorageService.storeProductImage(imageFile)).thenReturn(newFilename);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponse result = productService.uploadProductImage(productId, imageFile);

        // Then
        assertNotNull(result);
        verify(productRepository).findById(productId);
        verify(fileStorageService, never()).deleteProductImage(anyString());
        verify(fileStorageService).storeProductImage(imageFile);
        verify(productRepository).save(testProduct);
        assertEquals(newFilename, testProduct.getImg());
    }

    @Test
    @DisplayName("Should implement findById method correctly")
    void shouldImplementFindByIdMethodCorrectly() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.findById(productId);

        // Then
        assertNotNull(result);
        assertEquals(testProduct, result);
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should implement findByIdAndStateId method correctly")
    void shouldImplementFindByIdAndStateIdMethodCorrectly() {
        // Given
        Long productId = 1L;
        Long stateId = 1L;
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.findByIdAndStateId(productId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testProduct, result);
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should throw exception in findByIdAndStateId when states don't match")
    void shouldThrowExceptionInFindByIdAndStateIdWhenStatesDontMatch() {
        // Given
        Long productId = 1L;
        Long stateId = 2L; // Different from product's state (1L)
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> productService.findByIdAndStateId(productId, stateId));
        
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should use default state in findByIdAndStateId when stateId is null")
    void shouldUseDefaultStateInFindByIdAndStateIdWhenStateIdIsNull() {
        // Given
        Long productId = 1L;
        Long stateId = null;
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.findByIdAndStateId(productId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testProduct, result);
        verify(productRepository).findById(productId);
    }
}