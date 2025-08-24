package com.automo.dealProduct.repository;

import com.automo.dealProduct.entity.DealProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealProductRepository extends JpaRepository<DealProduct, Long> {
    
    List<DealProduct> findByStateId(Long stateId);
    List<DealProduct> findByDealId(Long dealId);
    List<DealProduct> findByProductId(Long productId);
} 