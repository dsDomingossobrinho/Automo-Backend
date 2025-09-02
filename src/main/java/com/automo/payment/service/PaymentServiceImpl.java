package com.automo.payment.service;

import com.automo.payment.dto.PaymentDto;
import com.automo.payment.entity.Payment;
import com.automo.payment.repository.PaymentRepository;
import com.automo.payment.response.PaymentResponse;
import com.automo.paymentType.entity.PaymentType;
import com.automo.paymentType.service.PaymentTypeService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTypeService paymentTypeService;
    private final StateService stateService;

    @Override
    public PaymentResponse createPayment(PaymentDto paymentDto) {
        PaymentType paymentType = paymentTypeService.findById(paymentDto.paymentTypeId());

        State state = stateService.findById(paymentDto.stateId());

        Payment payment = new Payment();
        payment.setDocument(paymentDto.document());
        payment.setIdentifier(paymentDto.identifier());
        payment.setPaymentType(paymentType);
        payment.setState(state);
        
        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse updatePayment(Long id, PaymentDto paymentDto) {
        Payment payment = this.getPaymentById(id);
        
        PaymentType paymentType = paymentTypeService.findById(paymentDto.paymentTypeId());

        State state = stateService.findById(paymentDto.stateId());

        payment.setDocument(paymentDto.document());
        payment.setIdentifier(paymentDto.identifier());
        payment.setPaymentType(paymentType);
        payment.setState(state);
        
        Payment updatedPayment = paymentRepository.save(payment);
        return mapToResponse(updatedPayment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment with ID " + id + " not found"));
    }

    @Override
    public PaymentResponse getPaymentByIdResponse(Long id) {
        Payment payment = this.getPaymentById(id);
        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByState(Long stateId) {
        return paymentRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByType(Long paymentTypeId) {
        return paymentRepository.findByPaymentTypeId(paymentTypeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByIdentifier(String identifier) {
        return paymentRepository.findByIdentifier(identifier).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment with ID " + id + " not found");
        }
        paymentRepository.deleteById(id);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getDocument(),
                payment.getIdentifier(),
                payment.getState().getId(),
                payment.getState().getState(),
                payment.getPaymentType().getId(),
                payment.getPaymentType().getType(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    @Override
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Payment with ID " + id + " not found"));
    }

    @Override
    public Payment findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Payment entity = paymentRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Payment with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Payment with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 