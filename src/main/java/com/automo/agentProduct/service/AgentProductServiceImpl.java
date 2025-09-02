package com.automo.agentProduct.service;

import com.automo.agent.entity.Agent;
import com.automo.agent.service.AgentService;
import com.automo.agentProduct.dto.AgentProductDto;
import com.automo.agentProduct.entity.AgentProduct;
import com.automo.agentProduct.repository.AgentProductRepository;
import com.automo.agentProduct.response.AgentProductResponse;
import com.automo.product.entity.Product;
import com.automo.product.service.ProductService;
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
    private final AgentService agentService;
    private final ProductService productService;

    @Override
    public AgentProductResponse createAgentProduct(AgentProductDto agentProductDto) {
        Agent agent = agentService.findById(agentProductDto.agentId());
        
        Product product = productService.findById(agentProductDto.productId());

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

        Agent agent = agentService.findById(agentProductDto.agentId());
        
        Product product = productService.findById(agentProductDto.productId());

        existingAgentProduct.setAgent(agent);
        existingAgentProduct.setProduct(product);

        AgentProduct updatedAgentProduct = agentProductRepository.save(existingAgentProduct);
        return mapToResponse(updatedAgentProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentProductResponse> getAllAgentProducts() {
        return agentProductRepository.findAllWithAgentAndProduct().stream()
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
        AgentProduct agentProduct = agentProductRepository.findByIdWithAgentAndProduct(id)
                .orElseThrow(() -> new RuntimeException("AgentProduct not found"));
        return mapToResponse(agentProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentProductResponse> getAgentProductsByAgent(Long agentId) {
        return agentProductRepository.findByAgentIdWithAgentAndProduct(agentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentProductResponse> getAgentProductsByProduct(Long productId) {
        return agentProductRepository.findByProductIdWithAgentAndProduct(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AgentProductResponse getAgentProductByAgentAndProduct(Long agentId, Long productId) {
        AgentProduct agentProduct = agentProductRepository.findByAgentIdAndProductIdWithAgentAndProduct(agentId, productId)
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

    @Override
    public AgentProduct findById(Long id) {
        return agentProductRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AgentProduct with ID " + id + " not found"));
    }

    @Override
    public AgentProduct findByIdAndStateId(Long id, Long stateId) {
        AgentProduct entity = agentProductRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AgentProduct with ID " + id + " not found"));
        
        // For entities without state relationship, return the entity regardless of stateId
        return entity;
    }
} 