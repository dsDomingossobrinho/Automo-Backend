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
} 