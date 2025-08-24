package com.automo.paymentType.service;

import com.automo.paymentType.dto.PaymentTypeDto;
import com.automo.paymentType.entity.PaymentType;
import com.automo.paymentType.repository.PaymentTypeRepository;
import com.automo.paymentType.response.PaymentTypeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentTypeServiceImpl implements PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;

    @Override
    public PaymentTypeResponse createPaymentType(PaymentTypeDto paymentTypeDto) {
        PaymentType paymentType = new PaymentType();
        paymentType.setType(paymentTypeDto.type());
        paymentType.setDescription(paymentTypeDto.description());
        
        PaymentType savedPaymentType = paymentTypeRepository.save(paymentType);
        return mapToResponse(savedPaymentType);
    }

    @Override
    public PaymentTypeResponse updatePaymentType(Long id, PaymentTypeDto paymentTypeDto) {
        PaymentType paymentType = this.getPaymentTypeById(id);
        
        paymentType.setType(paymentTypeDto.type());
        paymentType.setDescription(paymentTypeDto.description());
        
        PaymentType updatedPaymentType = paymentTypeRepository.save(paymentType);
        return mapToResponse(updatedPaymentType);
    }

    @Override
    public List<PaymentTypeResponse> getAllPaymentTypes() {
        return paymentTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentType getPaymentTypeById(Long id) {
        return paymentTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentType with ID " + id + " not found"));
    }

    @Override
    public PaymentTypeResponse getPaymentTypeByIdResponse(Long id) {
        PaymentType paymentType = this.getPaymentTypeById(id);
        return mapToResponse(paymentType);
    }

    @Override
    public void deletePaymentType(Long id) {
        if (!paymentTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("PaymentType with ID " + id + " not found");
        }
        paymentTypeRepository.deleteById(id);
    }

    private PaymentTypeResponse mapToResponse(PaymentType paymentType) {
        return new PaymentTypeResponse(
                paymentType.getId(),
                paymentType.getType(),
                paymentType.getDescription(),
                paymentType.getCreatedAt(),
                paymentType.getUpdatedAt()
        );
    }
} 