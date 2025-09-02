package com.automo.associatedEmail.service;

import com.automo.associatedEmail.dto.AssociatedEmailDto;
import com.automo.associatedEmail.entity.AssociatedEmail;
import com.automo.associatedEmail.repository.AssociatedEmailRepository;
import com.automo.associatedEmail.response.AssociatedEmailResponse;
import com.automo.identifier.entity.Identifier;
import com.automo.identifier.service.IdentifierService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.user.entity.User;
import com.automo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AssociatedEmailServiceImpl implements AssociatedEmailService {

    private final AssociatedEmailRepository associatedEmailRepository;
    private final IdentifierService identifierService;
    private final StateService stateService;
    private final UserService userService;

    @Override
    public AssociatedEmailResponse createAssociatedEmail(AssociatedEmailDto associatedEmailDto) {
        Identifier identifier = identifierService.findById(associatedEmailDto.identifierId());
        
        State state = stateService.findById(associatedEmailDto.stateId());

        AssociatedEmail associatedEmail = new AssociatedEmail();
        associatedEmail.setIdentifier(identifier);
        associatedEmail.setEmail(associatedEmailDto.email());
        associatedEmail.setState(state);

        AssociatedEmail savedAssociatedEmail = associatedEmailRepository.save(associatedEmail);
        return mapToResponse(savedAssociatedEmail);
    }

    @Override
    public AssociatedEmailResponse updateAssociatedEmail(Long id, AssociatedEmailDto associatedEmailDto) {
        AssociatedEmail existingAssociatedEmail = associatedEmailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AssociatedEmail not found"));

        Identifier identifier = identifierService.findById(associatedEmailDto.identifierId());
        
        State state = stateService.findById(associatedEmailDto.stateId());

        existingAssociatedEmail.setIdentifier(identifier);
        existingAssociatedEmail.setEmail(associatedEmailDto.email());
        existingAssociatedEmail.setState(state);

        AssociatedEmail updatedAssociatedEmail = associatedEmailRepository.save(existingAssociatedEmail);
        return mapToResponse(updatedAssociatedEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedEmailResponse> getAllAssociatedEmails() {
        State eliminatedState = stateService.getEliminatedState();
        return associatedEmailRepository.findAll().stream()
                .filter(email -> email.getState() != null && !email.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssociatedEmail getAssociatedEmailById(Long id) {
        return associatedEmailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AssociatedEmail not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public AssociatedEmailResponse getAssociatedEmailByIdResponse(Long id) {
        AssociatedEmail associatedEmail = getAssociatedEmailById(id);
        return mapToResponse(associatedEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedEmailResponse> getAssociatedEmailsByIdentifier(Long identifierId) {
        return associatedEmailRepository.findByIdentifierId(identifierId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedEmailResponse> getAssociatedEmailsByState(Long stateId) {
        return associatedEmailRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssociatedEmailResponse getAssociatedEmailByEmail(String email) {
        AssociatedEmail associatedEmail = associatedEmailRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("AssociatedEmail not found"));
        return mapToResponse(associatedEmail);
    }

    @Override
    public void deleteAssociatedEmail(Long id) {
        AssociatedEmail associatedEmail = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        associatedEmail.setState(eliminatedState);
        
        associatedEmailRepository.save(associatedEmail);
    }

    private AssociatedEmailResponse mapToResponse(AssociatedEmail associatedEmail) {
        // Buscar o usuário para obter o nome
        User user = null;
        try {
            user = userService.findById(associatedEmail.getIdentifier().getUserId());
        } catch (Exception e) {
            // User not found, will be null
        }
        
        return new AssociatedEmailResponse(
                associatedEmail.getId(),
                associatedEmail.getIdentifier().getId(),
                user != null ? user.getName() : null,
                associatedEmail.getEmail(),
                associatedEmail.getState().getId(),
                associatedEmail.getState().getState(),
                associatedEmail.getCreatedAt(),
                associatedEmail.getUpdatedAt()
        );
    }

    @Override
    public AssociatedEmail findById(Long id) {
        return associatedEmailRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AssociatedEmail with ID " + id + " not found"));
    }

    @Override
    public AssociatedEmail findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        AssociatedEmail entity = associatedEmailRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AssociatedEmail with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("AssociatedEmail with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 