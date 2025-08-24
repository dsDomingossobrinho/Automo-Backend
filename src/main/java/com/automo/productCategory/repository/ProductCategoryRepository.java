package com.automo.productCategory.repository;

import com.automo.productCategory.entity.ProductCategory;
import com.automo.productCategory.response.ProductCategoryResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    Optional<ProductCategory> findByCategory(String category);
    
    List<ProductCategory> findByStateId(Long stateId);
    
    boolean existsByCategory(String category);
    
    @Query("SELECT new com.automo.productCategory.response.ProductCategoryResponse(pc.id, pc.category, pc.description, pc.state.id, pc.state.state, pc.createdAt, pc.updatedAt) FROM ProductCategory pc")
    List<ProductCategoryResponse> findAllResponse();
    
    @Query("SELECT new com.automo.productCategory.response.ProductCategoryResponse(pc.id, pc.category, pc.description, pc.state.id, pc.state.state, pc.createdAt, pc.updatedAt) FROM ProductCategory pc WHERE pc.id = :id")
    Optional<ProductCategoryResponse> findResponseById(Long id);
} 