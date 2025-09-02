package com.automo.associatedContact.service;

import com.automo.associatedContact.dto.AssociatedContactDto;
import com.automo.associatedContact.entity.AssociatedContact;
import com.automo.associatedContact.repository.AssociatedContactRepository;
import com.automo.associatedContact.response.AssociatedContactResponse;
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
public class AssociatedContactServiceImpl implements AssociatedContactService {

    private final AssociatedContactRepository associatedContactRepository;
    private final IdentifierService identifierService;
    private final StateService stateService;
    private final UserService userService;

    @Override
    public AssociatedContactResponse createAssociatedContact(AssociatedContactDto associatedContactDto) {
        Identifier identifier = identifierService.findById(associatedContactDto.identifierId());
        
        State state = stateService.findById(associatedContactDto.stateId());

        AssociatedContact associatedContact = new AssociatedContact();
        associatedContact.setIdentifier(identifier);
        associatedContact.setContact(associatedContactDto.contact());
        associatedContact.setState(state);

        AssociatedContact savedAssociatedContact = associatedContactRepository.save(associatedContact);
        return mapToResponse(savedAssociatedContact);
    }

    @Override
    public AssociatedContactResponse updateAssociatedContact(Long id, AssociatedContactDto associatedContactDto) {
        AssociatedContact existingAssociatedContact = associatedContactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AssociatedContact not found"));

        Identifier identifier = identifierService.findById(associatedContactDto.identifierId());
        
        State state = stateService.findById(associatedContactDto.stateId());

        existingAssociatedContact.setIdentifier(identifier);
        existingAssociatedContact.setContact(associatedContactDto.contact());
        existingAssociatedContact.setState(state);

        AssociatedContact updatedAssociatedContact = associatedContactRepository.save(existingAssociatedContact);
        return mapToResponse(updatedAssociatedContact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedContactResponse> getAllAssociatedContacts() {
        return associatedContactRepository.findAllWithIdentifierAndState().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssociatedContact getAssociatedContactById(Long id) {
        return associatedContactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AssociatedContact not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public AssociatedContactResponse getAssociatedContactByIdResponse(Long id) {
        AssociatedContact associatedContact = associatedContactRepository.findByIdWithIdentifierAndState(id)
                .orElseThrow(() -> new RuntimeException("AssociatedContact not found"));
        return mapToResponse(associatedContact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedContactResponse> getAssociatedContactsByIdentifier(Long identifierId) {
        return associatedContactRepository.findByIdentifierIdWithIdentifierAndState(identifierId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedContactResponse> getAssociatedContactsByState(Long stateId) {
        return associatedContactRepository.findByStateIdWithIdentifierAndState(stateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAssociatedContact(Long id) {
        if (!associatedContactRepository.existsById(id)) {
            throw new RuntimeException("AssociatedContact not found");
        }
        associatedContactRepository.deleteById(id);
    }

    private AssociatedContactResponse mapToResponse(AssociatedContact associatedContact) {
        // Buscar o usuário para obter o nome
        User user = null;
        try {
            user = userService.findById(associatedContact.getIdentifier().getUserId());
        } catch (Exception e) {
            // User not found, will be null
        }
        
        return new AssociatedContactResponse(
                associatedContact.getId(),
                associatedContact.getIdentifier().getId(),
                user != null ? user.getName() : null,
                associatedContact.getContact(),
                associatedContact.getState().getId(),
                associatedContact.getState().getState(),
                associatedContact.getCreatedAt(),
                associatedContact.getUpdatedAt()
        );
    }

    @Override
    public AssociatedContact findById(Long id) {
        return associatedContactRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AssociatedContact with ID " + id + " not found"));
    }

    @Override
    public AssociatedContact findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        AssociatedContact entity = associatedContactRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AssociatedContact with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("AssociatedContact with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 