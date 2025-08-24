package com.automo.product.service;

import com.automo.product.dto.ProductDto;
import com.automo.product.entity.Product;
import com.automo.product.repository.ProductRepository;
import com.automo.product.response.ProductResponse;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StateRepository stateRepository;

    @Override
    public ProductResponse createProduct(ProductDto productDto) {
        State state = stateRepository.findById(productDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + productDto.stateId() + " not found"));

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
        
        State state = stateRepository.findById(productDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + productDto.stateId() + " not found"));

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
        return productRepository.findAll().stream()
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
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
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
} 