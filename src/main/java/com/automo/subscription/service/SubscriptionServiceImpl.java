package com.automo.subscription.service;

import com.automo.subscription.dto.SubscriptionDto;
import com.automo.subscription.entity.Subscription;
import com.automo.subscription.repository.SubscriptionRepository;
import com.automo.subscription.response.SubscriptionResponse;
import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import com.automo.subscriptionPlan.repository.SubscriptionPlanRepository;
import com.automo.promotion.entity.Promotion;
import com.automo.promotion.repository.PromotionRepository;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;
    private final PromotionRepository promotionRepository;
    private final StateRepository stateRepository;

    @Override
    @Transactional
    public SubscriptionResponse createSubscription(SubscriptionDto subscriptionDto) {
        User user = userRepository.findById(subscriptionDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + subscriptionDto.userId() + " not found"));

        SubscriptionPlan plan = planRepository.findById(subscriptionDto.planId())
                .orElseThrow(() -> new EntityNotFoundException("SubscriptionPlan with ID " + subscriptionDto.planId() + " not found"));

        State state = stateRepository.findById(subscriptionDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + subscriptionDto.stateId() + " not found"));

        // Verificar se existe subscrição ativa para o usuário
        List<Subscription> activeSubscriptions = subscriptionRepository.findByUserIdAndStateId(
                subscriptionDto.userId(), 
                stateRepository.findByState("ACTIVE").map(State::getId).orElse(1L)
        );

        // Se existir subscrição ativa, calcular dias restantes e estender a nova
        if (!activeSubscriptions.isEmpty()) {
            Subscription activeSubscription = activeSubscriptions.get(0);
            LocalDate currentDate = LocalDate.now();
            
            // Calcular dias restantes da subscrição ativa
            long remainingDays = 0;
            if (activeSubscription.getEndDate().isAfter(currentDate)) {
                remainingDays = ChronoUnit.DAYS.between(currentDate, activeSubscription.getEndDate());
            }
            
            // Desativar subscrição anterior
            State inactiveState = stateRepository.findByState("INACTIVE")
                    .orElseThrow(() -> new EntityNotFoundException("INACTIVE state not found"));
            activeSubscription.setState(inactiveState);
            subscriptionRepository.save(activeSubscription);
            
            // Calcular nova data de fim incluindo dias restantes
            LocalDate newEndDate = subscriptionDto.endDate().plusDays(remainingDays);
            
            // Criar nova subscrição com data estendida
            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setPlan(plan);
            subscription.setPrice(subscriptionDto.price());
            subscription.setStartDate(subscriptionDto.startDate());
            subscription.setEndDate(newEndDate);
            subscription.setMessageCount(subscriptionDto.messageCount());
            subscription.setState(state);
            
            if (subscriptionDto.promotionId() != null) {
                Promotion promotion = promotionRepository.findById(subscriptionDto.promotionId())
                        .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + subscriptionDto.promotionId() + " not found"));
                subscription.setPromotion(promotion);
            }
            
            Subscription savedSubscription = subscriptionRepository.save(subscription);
            return mapToResponse(savedSubscription);
        } else {
            // Criar subscrição normal (sem subscrição ativa existente)
            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setPlan(plan);
            subscription.setPrice(subscriptionDto.price());
            subscription.setStartDate(subscriptionDto.startDate());
            subscription.setEndDate(subscriptionDto.endDate());
            subscription.setMessageCount(subscriptionDto.messageCount());
            subscription.setState(state);
            
            if (subscriptionDto.promotionId() != null) {
                Promotion promotion = promotionRepository.findById(subscriptionDto.promotionId())
                        .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + subscriptionDto.promotionId() + " not found"));
                subscription.setPromotion(promotion);
            }
            
            Subscription savedSubscription = subscriptionRepository.save(subscription);
            return mapToResponse(savedSubscription);
        }
    }

    @Override
    public SubscriptionResponse updateSubscription(Long id, SubscriptionDto subscriptionDto) {
        Subscription subscription = this.getSubscriptionById(id);
        
        User user = userRepository.findById(subscriptionDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + subscriptionDto.userId() + " not found"));

        SubscriptionPlan plan = planRepository.findById(subscriptionDto.planId())
                .orElseThrow(() -> new EntityNotFoundException("SubscriptionPlan with ID " + subscriptionDto.planId() + " not found"));

        State state = stateRepository.findById(subscriptionDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + subscriptionDto.stateId() + " not found"));

        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(subscriptionDto.price());
        subscription.setStartDate(subscriptionDto.startDate());
        subscription.setEndDate(subscriptionDto.endDate());
        subscription.setMessageCount(subscriptionDto.messageCount());
        subscription.setState(state);
        
        if (subscriptionDto.promotionId() != null) {
            Promotion promotion = promotionRepository.findById(subscriptionDto.promotionId())
                    .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + subscriptionDto.promotionId() + " not found"));
            subscription.setPromotion(promotion);
        } else {
            subscription.setPromotion(null);
        }
        
        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return mapToResponse(updatedSubscription);
    }

    @Override
    public List<SubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Subscription getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription with ID " + id + " not found"));
    }

    @Override
    public SubscriptionResponse getSubscriptionByIdResponse(Long id) {
        Subscription subscription = this.getSubscriptionById(id);
        return mapToResponse(subscription);
    }

    @Override
    public List<SubscriptionResponse> getSubscriptionsByState(Long stateId) {
        return subscriptionRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SubscriptionResponse> getSubscriptionsByUser(Long userId) {
        return subscriptionRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SubscriptionResponse> getSubscriptionsByPlan(Long planId) {
        return subscriptionRepository.findByPlanId(planId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SubscriptionResponse> getSubscriptionsByPromotion(Long promotionId) {
        return subscriptionRepository.findByPromotionId(promotionId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SubscriptionResponse> getSubscriptionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return subscriptionRepository.findByStartDateBetween(startDate, endDate).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SubscriptionResponse> getExpiredSubscriptions(LocalDate date) {
        return subscriptionRepository.findByEndDateBefore(date).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new EntityNotFoundException("Subscription with ID " + id + " not found");
        }
        subscriptionRepository.deleteById(id);
    }

    private SubscriptionResponse mapToResponse(Subscription subscription) {
        // Buscar o usuário para obter o nome
        User user = userRepository.findById(subscription.getUser().getId())
                .orElse(null);
        
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getUser().getId(),
                user != null ? user.getName() : null,
                subscription.getPlan().getId(),
                subscription.getPlan().getName(),
                subscription.getPromotion() != null ? subscription.getPromotion().getId() : null,
                subscription.getPromotion() != null ? subscription.getPromotion().getName() : null,
                subscription.getPrice(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getMessageCount(),
                subscription.getState().getId(),
                subscription.getState().getState(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }
} 