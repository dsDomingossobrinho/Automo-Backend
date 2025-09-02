package com.automo.product.service;

import com.automo.product.dto.ProductDto;
import com.automo.product.entity.Product;
import com.automo.product.repository.ProductRepository;
import com.automo.product.response.ProductResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StateService stateService;

    @Override
    public ProductResponse createProduct(ProductDto productDto) {
        State state = stateService.findById(productDto.stateId());

        Product product = new Product();
        product.setName(productDto.name());
        product.setImg(productDto.img());
        product.setDescription(productDto.description());
        product.setPrice(productDto.price());
        product.setState(state);
        
        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductDto productDto) {
        Product product = this.getProductById(id);
        
        State state = stateService.findById(productDto.stateId());

        product.setName(productDto.name());
        product.setImg(productDto.img());
        product.setDescription(productDto.description());
        product.setPrice(productDto.price());
        product.setState(state);
        
        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        State eliminatedState = stateService.getEliminatedState();
        return productRepository.findAll().stream()
                .filter(product -> product.getState() != null && !product.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
    }

    @Override
    public ProductResponse getProductByIdResponse(Long id) {
        Product product = this.getProductById(id);
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getProductsByState(Long stateId) {
        return productRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        product.setState(eliminatedState);
        
        productRepository.save(product);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getImg(),
                product.getDescription(),
                product.getPrice(),
                product.getState().getId(),
                product.getState().getState(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product with ID " + id + " not found"));
    }

    @Override
    public Product findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Product with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 