package com.automo.deal.service;

import com.automo.deal.dto.DealDto;
import com.automo.deal.entity.Deal;
import com.automo.deal.repository.DealRepository;
import com.automo.deal.response.DealResponse;
import com.automo.lead.entity.Lead;
import com.automo.lead.service.LeadService;
import com.automo.promotion.entity.Promotion;
import com.automo.promotion.service.PromotionService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final LeadService leadService;
    private final PromotionService promotionService;
    private final StateService stateService;

    @Override
    public DealResponse createDeal(DealDto dealDto) {
        State state = stateService.findById(dealDto.stateId());

        Deal deal = new Deal();
        deal.setTotal(dealDto.total());
        deal.setDeliveryDate(dealDto.deliveryDate());
        deal.setMessageCount(dealDto.messageCount());
        deal.setState(state);
        
        if (dealDto.leadId() != null) {
            Lead lead = leadService.findById(dealDto.leadId());
            deal.setLead(lead);
        }
        
        if (dealDto.promotionId() != null) {
            Promotion promotion = promotionService.findById(dealDto.promotionId());
            deal.setPromotion(promotion);
        }
        
        Deal savedDeal = dealRepository.save(deal);
        return mapToResponse(savedDeal);
    }

    @Override
    public DealResponse updateDeal(Long id, DealDto dealDto) {
        Deal deal = this.getDealById(id);
        
        State state = stateService.findById(dealDto.stateId());

        deal.setTotal(dealDto.total());
        deal.setDeliveryDate(dealDto.deliveryDate());
        deal.setMessageCount(dealDto.messageCount());
        deal.setState(state);
        
        if (dealDto.leadId() != null) {
            Lead lead = leadService.findById(dealDto.leadId());
            deal.setLead(lead);
        } else {
            deal.setLead(null);
        }
        
        if (dealDto.promotionId() != null) {
            Promotion promotion = promotionService.findById(dealDto.promotionId());
            deal.setPromotion(promotion);
        } else {
            deal.setPromotion(null);
        }
        
        Deal updatedDeal = dealRepository.save(deal);
        return mapToResponse(updatedDeal);
    }

    @Override
    public List<DealResponse> getAllDeals() {
        return dealRepository.findAllWithRelations().stream()
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
        Deal deal = dealRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Deal with ID " + id + " not found"));
        return mapToResponse(deal);
    }

    @Override
    public List<DealResponse> getDealsByState(Long stateId) {
        return dealRepository.findByStateIdWithRelations(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<DealResponse> getDealsByIdentifier(Long identifierId) {
        return dealRepository.findByIdentifierIdWithRelations(identifierId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<DealResponse> getDealsByLead(Long leadId) {
        return dealRepository.findByLeadIdWithRelations(leadId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<DealResponse> getDealsByPromotion(Long promotionId) {
        return dealRepository.findByPromotionIdWithRelations(promotionId).stream()
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

    @Override
    public Deal findById(Long id) {
        return dealRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Deal with ID " + id + " not found"));
    }

    @Override
    public Deal findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Deal entity = dealRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Deal with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Deal with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 