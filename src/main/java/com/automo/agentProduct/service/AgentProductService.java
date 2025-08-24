package com.automo.agentProduct.service;

import com.automo.agentProduct.dto.AgentProductDto;
import com.automo.agentProduct.entity.AgentProduct;
import com.automo.agentProduct.response.AgentProductResponse;

import java.util.List;

public interface AgentProductService {

    AgentProductResponse createAgentProduct(AgentProductDto agentProductDto);

    AgentProductResponse updateAgentProduct(Long id, AgentProductDto agentProductDto);

    List<AgentProductResponse> getAllAgentProducts();

    AgentProduct getAgentProductById(Long id);

    AgentProductResponse getAgentProductByIdResponse(Long id);

    List<AgentProductResponse> getAgentProductsByAgent(Long agentId);

    List<AgentProductResponse> getAgentProductsByProduct(Long productId);

    AgentProductResponse getAgentProductByAgentAndProduct(Long agentId, Long productId);

    void deleteAgentProduct(Long id);
} 