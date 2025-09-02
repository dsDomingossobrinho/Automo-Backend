package com.automo.dealProduct.service;

import com.automo.dealProduct.dto.DealProductDto;
import com.automo.dealProduct.entity.DealProduct;
import com.automo.dealProduct.response.DealProductResponse;

import java.util.List;

public interface DealProductService {

    DealProductResponse createDealProduct(DealProductDto dealProductDto);

    DealProductResponse updateDealProduct(Long id, DealProductDto dealProductDto);

    List<DealProductResponse> getAllDealProducts();

    DealProduct getDealProductById(Long id);

    DealProductResponse getDealProductByIdResponse(Long id);

    List<DealProductResponse> getDealProductsByState(Long stateId);

    List<DealProductResponse> getDealProductsByDeal(Long dealId);

    List<DealProductResponse> getDealProductsByProduct(Long productId);

    void deleteDealProduct(Long id);
    
    /**
     * Busca DealProduct por ID - método obrigatório para comunicação entre services
     */
    DealProduct findById(Long id);
    
    /**
     * Busca DealProduct por ID e estado específico (state_id = 1 por padrão)
     */
    DealProduct findByIdAndStateId(Long id, Long stateId);
} 