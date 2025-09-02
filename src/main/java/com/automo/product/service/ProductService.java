package com.automo.product.service;

import com.automo.product.dto.ProductDto;
import com.automo.product.entity.Product;
import com.automo.product.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductDto productDto);

    ProductResponse updateProduct(Long id, ProductDto productDto);

    List<ProductResponse> getAllProducts();

    Product getProductById(Long id);

    ProductResponse getProductByIdResponse(Long id);

    List<ProductResponse> getProductsByState(Long stateId);

    void deleteProduct(Long id);
    
    /**
     * Busca Product por ID - método obrigatório para comunicação entre services
     */
    Product findById(Long id);
    
    /**
     * Busca Product por ID e estado específico (state_id = 1 por padrão)
     */
    Product findByIdAndStateId(Long id, Long stateId);
} 