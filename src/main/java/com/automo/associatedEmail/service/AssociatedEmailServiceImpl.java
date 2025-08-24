package com.automo.associatedEmail.service;

import com.automo.associatedEmail.dto.AssociatedEmailDto;
import com.automo.associatedEmail.entity.AssociatedEmail;
import com.automo.associatedEmail.repository.AssociatedEmailRepository;
import com.automo.associatedEmail.response.AssociatedEmailResponse;
import com.automo.identifier.entity.Identifier;
import com.automo.identifier.repository.IdentifierRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
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
    private final IdentifierRepository identifierRepository;
    private final StateRepository stateRepository;
    private final UserRepository userRepository;

    @Override
    public AssociatedEmailResponse createAssociatedEmail(AssociatedEmailDto associatedEmailDto) {
        Identifier identifier = identifierRepository.findById(associatedEmailDto.identifierId())
                .orElseThrow(() -> new RuntimeException("Identifier not found"));
        
        State state = stateRepository.findById(associatedEmailDto.stateId())
                .orElseThrow(() -> new RuntimeException("State not found"));

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

        Identifier identifier = identifierRepository.findById(associatedEmailDto.identifierId())
                .orElseThrow(() -> new RuntimeException("Identifier not found"));
        
        State state = stateRepository.findById(associatedEmailDto.stateId())
                .orElseThrow(() -> new RuntimeException("State not found"));

        existingAssociatedEmail.setIdentifier(identifier);
        existingAssociatedEmail.setEmail(associatedEmailDto.email());
        existingAssociatedEmail.setState(state);

        AssociatedEmail updatedAssociatedEmail = associatedEmailRepository.save(existingAssociatedEmail);
        return mapToResponse(updatedAssociatedEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedEmailResponse> getAllAssociatedEmails() {
        return associatedEmailRepository.findAll().stream()
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
        if (!associatedEmailRepository.existsById(id)) {
            throw new RuntimeException("AssociatedEmail not found");
        }
        associatedEmailRepository.deleteById(id);
    }

    private AssociatedEmailResponse mapToResponse(AssociatedEmail associatedEmail) {
        // Buscar o usuário para obter o nome
        User user = userRepository.findById(associatedEmail.getIdentifier().getUserId())
                .orElse(null);
        
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
} 