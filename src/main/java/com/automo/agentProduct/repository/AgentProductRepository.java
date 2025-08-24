package com.automo.agentProduct.repository;

import com.automo.agentProduct.entity.AgentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentProductRepository extends JpaRepository<AgentProduct, Long> {
    
    List<AgentProduct> findByAgentId(Long agentId);
    List<AgentProduct> findByProductId(Long productId);
    Optional<AgentProduct> findByAgentIdAndProductId(Long agentId, Long productId);
} 