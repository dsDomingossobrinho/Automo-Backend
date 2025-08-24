package com.automo.deal.service;

import com.automo.deal.dto.DealDto;
import com.automo.deal.entity.Deal;
import com.automo.deal.repository.DealRepository;
import com.automo.deal.response.DealResponse;
import com.automo.lead.entity.Lead;
import com.automo.lead.repository.LeadRepository;
import com.automo.promotion.entity.Promotion;
import com.automo.promotion.repository.PromotionRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final LeadRepository leadRepository;
    private final PromotionRepository promotionRepository;
    private final StateRepository stateRepository;

    @Override
    public DealResponse createDeal(DealDto dealDto) {
        State state = stateRepository.findById(dealDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + dealDto.stateId() + " not found"));

        Deal deal = new Deal();
        deal.setTotal(dealDto.total());
        deal.setDeliveryDate(dealDto.deliveryDate());
        deal.setMessageCount(dealDto.messageCount());
        deal.setState(state);
        
        if (dealDto.leadId() != null) {
            Lead lead = leadRepository.findById(dealDto.leadId())
                    .orElseThrow(() -> new EntityNotFoundException("Lead with ID " + dealDto.leadId() + " not found"));
            deal.setLead(lead);
        }
        
        if (dealDto.promotionId() != null) {
            Promotion promotion = promotionRepository.findById(dealDto.promotionId())
                    .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + dealDto.promotionId() + " not found"));
            deal.setPromotion(promotion);
        }
        
        Deal savedDeal = dealRepository.save(deal);
        return mapToResponse(savedDeal);
    }

    @Override
    public DealResponse updateDeal(Long id, DealDto dealDto) {
        Deal deal = this.getDealById(id);
        
        State state = stateRepository.findById(dealDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + dealDto.stateId() + " not found"));

        deal.setTotal(dealDto.total());
        deal.setDeliveryDate(dealDto.deliveryDate());
        deal.setMessageCount(dealDto.messageCount());
        deal.setState(state);
        
        if (dealDto.leadId() != null) {
            Lead lead = leadRepository.findById(dealDto.leadId())
                    .orElseThrow(() -> new EntityNotFoundException("Lead with ID " + dealDto.leadId() + " not found"));
            deal.setLead(lead);
        } else {
            deal.setLead(null);
        }
        
        if (dealDto.promotionId() != null) {
            Promotion promotion = promotionRepository.findById(dealDto.promotionId())
                    .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + dealDto.promotionId() + " not found"));
            deal.setPromotion(promotion);
        } else {
            deal.setPromotion(null);
        }
        
        Deal updatedDeal = dealRepository.save(deal);
        return mapToResponse(updatedDeal);
    }

    @Override
    public List<DealResponse> getAllDeals() {
        return dealRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Deal getDealById(Long id) {
        return dealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deal with ID " + id + " not found"));
    }

    @Override
    public DealResponse getDealByIdResponse(Long id) {
        Deal deal = this.getDealById(id);
        return mapToResponse(deal);
    }

    @Override
    public List<DealResponse> getDealsByState(Long stateId) {
        return dealRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<DealResponse> getDealsByIdentifier(Long identifierId) {
        return dealRepository.findByIdentifierId(identifierId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<DealResponse> getDealsByLead(Long leadId) {
        return dealRepository.findByLeadId(leadId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<DealResponse> getDealsByPromotion(Long promotionId) {
        return dealRepository.findByPromotionId(promotionId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteDeal(Long id) {
        if (!dealRepository.existsById(id)) {
            throw new EntityNotFoundException("Deal with ID " + id + " not found");
        }
        dealRepository.deleteById(id);
    }

    private DealResponse mapToResponse(Deal deal) {
        return new DealResponse(
                deal.getId(),
                deal.getIdentifier() != null ? deal.getIdentifier().getId() : null,
                deal.getLead() != null ? deal.getLead().getId() : null,
                deal.getLead() != null ? deal.getLead().getName() : null,
                deal.getPromotion() != null ? deal.getPromotion().getId() : null,
                deal.getPromotion() != null ? deal.getPromotion().getName() : null,
                deal.getTotal(),
                deal.getDeliveryDate(),
                deal.getMessageCount(),
                deal.getState().getId(),
                deal.getState().getState(),
                deal.getCreatedAt(),
                deal.getUpdatedAt()
        );
    }
} 