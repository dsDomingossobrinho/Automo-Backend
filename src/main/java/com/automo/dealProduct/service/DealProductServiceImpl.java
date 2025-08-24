package com.automo.dealProduct.service;

import com.automo.dealProduct.dto.DealProductDto;
import com.automo.dealProduct.entity.DealProduct;
import com.automo.dealProduct.repository.DealProductRepository;
import com.automo.dealProduct.response.DealProductResponse;
import com.automo.deal.entity.Deal;
import com.automo.deal.repository.DealRepository;
import com.automo.product.entity.Product;
import com.automo.product.repository.ProductRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealProductServiceImpl implements DealProductService {

    private final DealProductRepository dealProductRepository;
    private final DealRepository dealRepository;
    private final ProductRepository productRepository;
    private final StateRepository stateRepository;

    @Override
    public DealProductResponse createDealProduct(DealProductDto dealProductDto) {
        Deal deal = dealRepository.findById(dealProductDto.dealId())
                .orElseThrow(() -> new EntityNotFoundException("Deal with ID " + dealProductDto.dealId() + " not found"));

        Product product = productRepository.findById(dealProductDto.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + dealProductDto.productId() + " not found"));

        State state = stateRepository.findById(dealProductDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + dealProductDto.stateId() + " not found"));

        DealProduct dealProduct = new DealProduct();
        dealProduct.setDeal(deal);
        dealProduct.setProduct(product);
        dealProduct.setState(state);
        
        DealProduct savedDealProduct = dealProductRepository.save(dealProduct);
        return mapToResponse(savedDealProduct);
    }

    @Override
    public DealProductResponse updateDealProduct(Long id, DealProductDto dealProductDto) {
        DealProduct dealProduct = this.getDealProductById(id);
        
        Deal deal = dealRepository.findById(dealProductDto.dealId())
                .orElseThrow(() -> new EntityNotFoundException("Deal with ID " + dealProductDto.dealId() + " not found"));

        Product product = productRepository.findById(dealProductDto.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + dealProductDto.productId() + " not found"));

        State state = stateRepository.findById(dealProductDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + dealProductDto.stateId() + " not found"));

        dealProduct.setDeal(deal);
        dealProduct.setProduct(product);
        dealProduct.setState(state);
        
        DealProduct updatedDealProduct = dealProductRepository.save(dealProduct);
        return mapToResponse(updatedDealProduct);
    }

    @Override
    public List<DealProductResponse> getAllDealProducts() {
        return dealProductRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DealProduct getDealProductById(Long id) {
        return dealProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DealProduct with ID " + id + " not found"));
    }

    @Override
    public DealProductResponse getDealProductByIdResponse(Long id) {
        DealProduct dealProduct = this.getDealProductById(id);
        return mapToResponse(dealProduct);
    }

    @Override
    public List<DealProductResponse> getDealProductsByState(Long stateId) {
        return dealProductRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<DealProductResponse> getDealProductsByDeal(Long dealId) {
        return dealProductRepository.findByDealId(dealId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<DealProductResponse> getDealProductsByProduct(Long productId) {
        return dealProductRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteDealProduct(Long id) {
        if (!dealProductRepository.existsById(id)) {
            throw new EntityNotFoundException("DealProduct with ID " + id + " not found");
        }
        dealProductRepository.deleteById(id);
    }

    private DealProductResponse mapToResponse(DealProduct dealProduct) {
        return new DealProductResponse(
                dealProduct.getId(),
                dealProduct.getDeal().getId(),
                dealProduct.getDeal().getTotal().toString(),
                dealProduct.getProduct().getId(),
                dealProduct.getProduct().getName(),
                dealProduct.getState().getId(),
                dealProduct.getState().getState(),
                dealProduct.getCreatedAt(),
                dealProduct.getUpdatedAt()
        );
    }
} 