package com.automo.payment.service;

import com.automo.identifier.entity.Identifier;
import com.automo.identifier.service.IdentifierService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTypeService paymentTypeService;
    private final StateService stateService;
    private final IdentifierService identifierService;
    private final FileStorageService fileStorageService;

    @Override
    public PaymentResponse createPayment(PaymentDto paymentDto) {
        PaymentType paymentType = paymentTypeService.findById(paymentDto.paymentTypeId());
        State state = stateService.findById(paymentDto.stateId());
        Identifier identifier = identifierService.findById(paymentDto.identifierId());

        Payment payment = new Payment();
        payment.setDocument(paymentDto.document());
        payment.setIdentifier(identifier);
        payment.setPaymentType(paymentType);
        payment.setState(state);
        payment.setAmount(paymentDto.amount());
        
        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse updatePayment(Long id, PaymentDto paymentDto) {
        Payment payment = this.getPaymentById(id);
        
        PaymentType paymentType = paymentTypeService.findById(paymentDto.paymentTypeId());
        State state = stateService.findById(paymentDto.stateId());
        Identifier identifier = identifierService.findById(paymentDto.identifierId());

        payment.setDocument(paymentDto.document());
        payment.setIdentifier(identifier);
        payment.setPaymentType(paymentType);
        payment.setState(state);
        payment.setAmount(paymentDto.amount());
        
        Payment updatedPayment = paymentRepository.save(payment);
        return mapToResponse(updatedPayment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        State eliminatedState = stateService.getEliminatedState();
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getState() != null && !payment.getState().getId().equals(eliminatedState.getId()))
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
    public List<PaymentResponse> getPaymentsByIdentifier(Long identifierId) {
        return paymentRepository.findByIdentifierId(identifierId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        payment.setState(eliminatedState);
        
        paymentRepository.save(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getDocument(),
                payment.getIdentifier().getId(),
                payment.getState().getId(),
                payment.getState().getState(),
                payment.getPaymentType().getId(),
                payment.getPaymentType().getType(),
                payment.getAmount(),
                payment.getImageFilename(),
                payment.getOriginalFilename(),
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

    @Override
    public PaymentResponse createPaymentWithFile(Long identifierId, Long stateId, Long paymentTypeId, java.math.BigDecimal amount, MultipartFile file) {
        log.info("Creating payment with file for identifier ID: {}", identifierId);
        
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required for payment");
        }

        // Validar e armazenar ficheiro
        String uniqueFilename = fileStorageService.storePaymentFile(file);
        String originalFilename = file.getOriginalFilename();

        // Buscar entidades relacionadas
        State state = stateService.findById(stateId);
        PaymentType paymentType = paymentTypeService.findById(paymentTypeId);
        Identifier identifier = identifierService.findById(identifierId);

        // Criar pagamento
        Payment payment = new Payment();
        payment.setIdentifier(identifier);
        payment.setDocument("PAY-" + identifierId); // Generate document based on identifier ID
        payment.setState(state);
        payment.setPaymentType(paymentType);
        payment.setAmount(amount);
        payment.setImageFilename(uniqueFilename);
        payment.setOriginalFilename(originalFilename);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", savedPayment.getId());
        
        return mapToResponse(savedPayment);
    }

    @Override
    public PaymentResponse updatePaymentState(Long paymentId, Long stateId) {
        log.info("Updating payment state for ID: {} to state: {}", paymentId, stateId);
        
        Payment payment = this.getPaymentById(paymentId);
        State newState = stateService.findById(stateId);
        
        payment.setState(newState);
        Payment updatedPayment = paymentRepository.save(payment);
        
        log.info("Payment state updated successfully for ID: {}", paymentId);
        return mapToResponse(updatedPayment);
    }

    @Override
    public PaymentResponse updatePaymentType(Long paymentId, Long paymentTypeId) {
        log.info("Updating payment type for ID: {} to type: {}", paymentId, paymentTypeId);
        
        Payment payment = this.getPaymentById(paymentId);
        PaymentType newPaymentType = paymentTypeService.findById(paymentTypeId);
        
        payment.setPaymentType(newPaymentType);
        Payment updatedPayment = paymentRepository.save(payment);
        
        log.info("Payment type updated successfully for ID: {}", paymentId);
        return mapToResponse(updatedPayment);
    }

    @Override
    public PaymentResponse updatePaymentFile(Long paymentId, MultipartFile file) {
        log.info("Updating payment file for ID: {}", paymentId);
        
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required for update");
        }

        Payment payment = this.getPaymentById(paymentId);
        
        // Remover ficheiro antigo se existir
        if (payment.getImageFilename() != null) {
            fileStorageService.deletePaymentFile(payment.getImageFilename());
        }

        // Armazenar novo ficheiro
        String uniqueFilename = fileStorageService.storePaymentFile(file);
        String originalFilename = file.getOriginalFilename();

        payment.setImageFilename(uniqueFilename);
        payment.setOriginalFilename(originalFilename);
        
        Payment updatedPayment = paymentRepository.save(payment);
        
        log.info("Payment file updated successfully for ID: {}", paymentId);
        return mapToResponse(updatedPayment);
    }
} 