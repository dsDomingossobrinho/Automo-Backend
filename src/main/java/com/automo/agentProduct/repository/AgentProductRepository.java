package com.automo.agentProduct.repository;

import com.automo.agentProduct.entity.AgentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentProductRepository extends JpaRepository<AgentProduct, Long> {
    
    /**
     * Find all agent products with agent and product data eagerly loaded
     */
    @Query("SELECT ap FROM AgentProduct ap LEFT JOIN FETCH ap.agent LEFT JOIN FETCH ap.product")
    List<AgentProduct> findAllWithAgentAndProduct();
    
    /**
     * Find agent product by ID with agent and product data eagerly loaded
     */
    @Query("SELECT ap FROM AgentProduct ap LEFT JOIN FETCH ap.agent LEFT JOIN FETCH ap.product WHERE ap.id = :id")
    Optional<AgentProduct> findByIdWithAgentAndProduct(@Param("id") Long id);
    
    /**
     * Find agent products by agent ID with agent and product data eagerly loaded
     */
    @Query("SELECT ap FROM AgentProduct ap LEFT JOIN FETCH ap.agent LEFT JOIN FETCH ap.product WHERE ap.agent.id = :agentId")
    List<AgentProduct> findByAgentIdWithAgentAndProduct(@Param("agentId") Long agentId);
    
    /**
     * Find agent products by product ID with agent and product data eagerly loaded
     */
    @Query("SELECT ap FROM AgentProduct ap LEFT JOIN FETCH ap.agent LEFT JOIN FETCH ap.product WHERE ap.product.id = :productId")
    List<AgentProduct> findByProductIdWithAgentAndProduct(@Param("productId") Long productId);
    
    /**
     * Find agent product by agent and product IDs with agent and product data eagerly loaded
     */
    @Query("SELECT ap FROM AgentProduct ap LEFT JOIN FETCH ap.agent LEFT JOIN FETCH ap.product WHERE ap.agent.id = :agentId AND ap.product.id = :productId")
    Optional<AgentProduct> findByAgentIdAndProductIdWithAgentAndProduct(@Param("agentId") Long agentId, @Param("productId") Long productId);
    
    // Keep original methods for backward compatibility
    List<AgentProduct> findByAgentId(Long agentId);
    List<AgentProduct> findByProductId(Long productId);
    Optional<AgentProduct> findByAgentIdAndProductId(Long agentId, Long productId);
} 