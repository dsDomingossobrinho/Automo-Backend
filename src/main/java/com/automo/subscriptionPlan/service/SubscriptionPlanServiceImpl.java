package com.automo.subscriptionPlan.service;

import com.automo.subscriptionPlan.dto.SubscriptionPlanDto;
import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import com.automo.subscriptionPlan.repository.SubscriptionPlanRepository;
import com.automo.subscriptionPlan.response.SubscriptionPlanResponse;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final StateRepository stateRepository;

    @Override
    public SubscriptionPlanResponse createSubscriptionPlan(SubscriptionPlanDto subscriptionPlanDto) {
        State state = stateRepository.findById(subscriptionPlanDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + subscriptionPlanDto.stateId() + " not found"));

        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
        subscriptionPlan.setName(subscriptionPlanDto.name());
        subscriptionPlan.setPrice(subscriptionPlanDto.price());
        subscriptionPlan.setDescription(subscriptionPlanDto.description());
        subscriptionPlan.setState(state);
        
        SubscriptionPlan savedSubscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
        return mapToResponse(savedSubscriptionPlan);
    }

    @Override
    public SubscriptionPlanResponse updateSubscriptionPlan(Long id, SubscriptionPlanDto subscriptionPlanDto) {
        SubscriptionPlan subscriptionPlan = this.getSubscriptionPlanById(id);

        State state = stateRepository.findById(subscriptionPlanDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + subscriptionPlanDto.stateId() + " not found"));

        subscriptionPlan.setName(subscriptionPlanDto.name());
        subscriptionPlan.setPrice(subscriptionPlanDto.price());
        subscriptionPlan.setDescription(subscriptionPlanDto.description());
        subscriptionPlan.setState(state);
        
        SubscriptionPlan updatedSubscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
        return mapToResponse(updatedSubscriptionPlan);
    }

    @Override
    public List<SubscriptionPlanResponse> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlan getSubscriptionPlanById(Long id) {
        return subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SubscriptionPlan with ID " + id + " not found"));
    }

    @Override
    public SubscriptionPlanResponse getSubscriptionPlanByIdResponse(Long id) {
        SubscriptionPlan subscriptionPlan = this.getSubscriptionPlanById(id);
        return mapToResponse(subscriptionPlan);
    }

    @Override
    public List<SubscriptionPlanResponse> getSubscriptionPlansByState(Long stateId) {
        return subscriptionPlanRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSubscriptionPlan(Long id) {
        if (!subscriptionPlanRepository.existsById(id)) {
            throw new EntityNotFoundException("SubscriptionPlan with ID " + id + " not found");
        }
        subscriptionPlanRepository.deleteById(id);
    }

    private SubscriptionPlanResponse mapToResponse(SubscriptionPlan subscriptionPlan) {
        return new SubscriptionPlanResponse(
                subscriptionPlan.getId(),
                subscriptionPlan.getName(),
                subscriptionPlan.getPrice(),
                subscriptionPlan.getDescription(),
                subscriptionPlan.getState().getId(),
                subscriptionPlan.getState().getState(),
                subscriptionPlan.getCreatedAt(),
                subscriptionPlan.getUpdatedAt()
        );
    }
} 