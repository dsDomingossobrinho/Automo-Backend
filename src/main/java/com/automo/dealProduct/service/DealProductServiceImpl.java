package com.automo.dealProduct.service;

import com.automo.dealProduct.dto.DealProductDto;
import com.automo.dealProduct.entity.DealProduct;
import com.automo.dealProduct.repository.DealProductRepository;
import com.automo.dealProduct.response.DealProductResponse;
import com.automo.deal.entity.Deal;
import com.automo.deal.service.DealService;
import com.automo.product.entity.Product;
import com.automo.product.service.ProductService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealProductServiceImpl implements DealProductService {

    private final DealProductRepository dealProductRepository;
    private final DealService dealService;
    private final ProductService productService;
    private final StateService stateService;

    @Override
    public DealProductResponse createDealProduct(DealProductDto dealProductDto) {
        Deal deal = dealService.findById(dealProductDto.dealId());

        Product product = productService.findById(dealProductDto.productId());

        State state = stateService.findById(dealProductDto.stateId());

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
        
        Deal deal = dealService.findById(dealProductDto.dealId());

        Product product = productService.findById(dealProductDto.productId());

        State state = stateService.findById(dealProductDto.stateId());

        dealProduct.setDeal(deal);
        dealProduct.setProduct(product);
        dealProduct.setState(state);
        
        DealProduct updatedDealProduct = dealProductRepository.save(dealProduct);
        return mapToResponse(updatedDealProduct);
    }

    @Override
    public List<DealProductResponse> getAllDealProducts() {
        State eliminatedState = stateService.getEliminatedState();
        return dealProductRepository.findAll().stream()
                .filter(dealProduct -> dealProduct.getState() != null && !dealProduct.getState().getId().equals(eliminatedState.getId()))
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
        DealProduct dealProduct = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        dealProduct.setState(eliminatedState);
        
        dealProductRepository.save(dealProduct);
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

    @Override
    public DealProduct findById(Long id) {
        return dealProductRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("DealProduct with ID " + id + " not found"));
    }

    @Override
    public DealProduct findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        DealProduct entity = dealProductRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("DealProduct with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("DealProduct with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 