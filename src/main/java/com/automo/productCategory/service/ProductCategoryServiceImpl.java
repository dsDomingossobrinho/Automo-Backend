package com.automo.productCategory.service;

import com.automo.productCategory.dto.ProductCategoryDto;
import com.automo.productCategory.entity.ProductCategory;
import com.automo.productCategory.repository.ProductCategoryRepository;
import com.automo.productCategory.response.ProductCategoryResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final StateService stateService;

    @Override
    public ProductCategoryResponse createProductCategory(ProductCategoryDto productCategoryDto) {
        State state = stateService.findById(productCategoryDto.stateId());

        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(productCategoryDto.category());
        productCategory.setDescription(productCategoryDto.description());
        productCategory.setState(state);
        
        ProductCategory savedProductCategory = productCategoryRepository.save(productCategory);
        return mapToResponse(savedProductCategory);
    }

    @Override
    public ProductCategoryResponse updateProductCategory(Long id, ProductCategoryDto productCategoryDto) {
        ProductCategory productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductCategory with ID " + id + " not found"));

        State state = stateService.findById(productCategoryDto.stateId());

        productCategory.setCategory(productCategoryDto.category());
        productCategory.setDescription(productCategoryDto.description());
        productCategory.setState(state);
        
        ProductCategory updatedProductCategory = productCategoryRepository.save(productCategory);
        return mapToResponse(updatedProductCategory);
    }

    @Override
    public List<ProductCategoryResponse> getAllProductCategories() {
        return productCategoryRepository.findAllResponse();
    }

    @Override
    public ProductCategory getProductCategoryById(Long id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductCategory with ID " + id + " not found"));
    }

    @Override
    public ProductCategoryResponse getProductCategoryByIdResponse(Long id) {
        ProductCategory productCategory = this.getProductCategoryById(id);
        return mapToResponse(productCategory);
    }

    @Override
    public List<ProductCategoryResponse> getProductCategoriesByState(Long stateId) {
        return productCategoryRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteProductCategory(Long id) {
        if (!productCategoryRepository.existsById(id)) {
            throw new EntityNotFoundException("ProductCategory with ID " + id + " not found");
        }
        productCategoryRepository.deleteById(id);
    }

    private ProductCategoryResponse mapToResponse(ProductCategory productCategory) {
        return new ProductCategoryResponse(
                productCategory.getId(),
                productCategory.getCategory(),
                productCategory.getDescription(),
                productCategory.getState().getId(),
                productCategory.getState().getState(),
                productCategory.getCreatedAt(),
                productCategory.getUpdatedAt()
        );
    }

    @Override
    public ProductCategory findById(Long id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("ProductCategory with ID " + id + " not found"));
    }

    @Override
    public ProductCategory findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        ProductCategory entity = productCategoryRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("ProductCategory with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("ProductCategory with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 