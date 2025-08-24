package com.automo.productCategory.service;

import com.automo.productCategory.dto.ProductCategoryDto;
import com.automo.productCategory.entity.ProductCategory;
import com.automo.productCategory.response.ProductCategoryResponse;

import java.util.List;

public interface ProductCategoryService {

    ProductCategoryResponse createProductCategory(ProductCategoryDto productCategoryDto);

    ProductCategoryResponse updateProductCategory(Long id, ProductCategoryDto productCategoryDto);

    List<ProductCategoryResponse> getAllProductCategories();

    ProductCategory getProductCategoryById(Long id);

    ProductCategoryResponse getProductCategoryByIdResponse(Long id);

    List<ProductCategoryResponse> getProductCategoriesByState(Long stateId);

    void deleteProductCategory(Long id);
} 