package com.automo.agentProduct.service;

import com.automo.agent.entity.Agent;
import com.automo.agent.repository.AgentRepository;
import com.automo.agentProduct.dto.AgentProductDto;
import com.automo.agentProduct.entity.AgentProduct;
import com.automo.agentProduct.repository.AgentProductRepository;
import com.automo.agentProduct.response.AgentProductResponse;
import com.automo.product.entity.Product;
import com.automo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AgentProductServiceImpl implements AgentProductService {

    private final AgentProductRepository agentProductRepository;
    private final AgentRepository agentRepository;
    private final ProductRepository productRepository;

    @Override
    public AgentProductResponse createAgentProduct(AgentProductDto agentProductDto) {
        Agent agent = agentRepository.findById(agentProductDto.agentId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        
        Product product = productRepository.findById(agentProductDto.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        AgentProduct agentProduct = new AgentProduct();
        agentProduct.setAgent(agent);
        agentProduct.setProduct(product);

        AgentProduct savedAgentProduct = agentProductRepository.save(agentProduct);
        return mapToResponse(savedAgentProduct);
    }

    @Override
    public AgentProductResponse updateAgentProduct(Long id, AgentProductDto agentProductDto) {
        AgentProduct existingAgentProduct = agentProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AgentProduct not found"));

        Agent agent = agentRepository.findById(agentProductDto.agentId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        
        Product product = productRepository.findById(agentProductDto.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingAgentProduct.setAgent(agent);
        existingAgentProduct.setProduct(product);

        AgentProduct updatedAgentProduct = agentProductRepository.save(existingAgentProduct);
        return mapToResponse(updatedAgentProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentProductResponse> getAllAgentProducts() {
        return agentProductRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AgentProduct getAgentProductById(Long id) {
        return agentProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AgentProduct not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public AgentProductResponse getAgentProductByIdResponse(Long id) {
        AgentProduct agentProduct = getAgentProductById(id);
        return mapToResponse(agentProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentProductResponse> getAgentProductsByAgent(Long agentId) {
        return agentProductRepository.findByAgentId(agentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentProductResponse> getAgentProductsByProduct(Long productId) {
        return agentProductRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AgentProductResponse getAgentProductByAgentAndProduct(Long agentId, Long productId) {
        AgentProduct agentProduct = agentProductRepository.findByAgentIdAndProductId(agentId, productId)
                .orElseThrow(() -> new RuntimeException("AgentProduct not found"));
        return mapToResponse(agentProduct);
    }

    @Override
    public void deleteAgentProduct(Long id) {
        if (!agentProductRepository.existsById(id)) {
            throw new RuntimeException("AgentProduct not found");
        }
        agentProductRepository.deleteById(id);
    }

    private AgentProductResponse mapToResponse(AgentProduct agentProduct) {
        return new AgentProductResponse(
                agentProduct.getId(),
                agentProduct.getAgent().getId(),
                agentProduct.getAgent().getName(),
                agentProduct.getProduct().getId(),
                agentProduct.getProduct().getName(),
                agentProduct.getCreatedAt(),
                agentProduct.getUpdatedAt()
        );
    }
} 