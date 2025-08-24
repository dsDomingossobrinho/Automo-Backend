package com.automo.associatedContact.service;

import com.automo.associatedContact.dto.AssociatedContactDto;
import com.automo.associatedContact.entity.AssociatedContact;
import com.automo.associatedContact.repository.AssociatedContactRepository;
import com.automo.associatedContact.response.AssociatedContactResponse;
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
public class AssociatedContactServiceImpl implements AssociatedContactService {

    private final AssociatedContactRepository associatedContactRepository;
    private final IdentifierRepository identifierRepository;
    private final StateRepository stateRepository;
    private final UserRepository userRepository;

    @Override
    public AssociatedContactResponse createAssociatedContact(AssociatedContactDto associatedContactDto) {
        Identifier identifier = identifierRepository.findById(associatedContactDto.identifierId())
                .orElseThrow(() -> new RuntimeException("Identifier not found"));
        
        State state = stateRepository.findById(associatedContactDto.stateId())
                .orElseThrow(() -> new RuntimeException("State not found"));

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

        Identifier identifier = identifierRepository.findById(associatedContactDto.identifierId())
                .orElseThrow(() -> new RuntimeException("Identifier not found"));
        
        State state = stateRepository.findById(associatedContactDto.stateId())
                .orElseThrow(() -> new RuntimeException("State not found"));

        existingAssociatedContact.setIdentifier(identifier);
        existingAssociatedContact.setContact(associatedContactDto.contact());
        existingAssociatedContact.setState(state);

        AssociatedContact updatedAssociatedContact = associatedContactRepository.save(existingAssociatedContact);
        return mapToResponse(updatedAssociatedContact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedContactResponse> getAllAssociatedContacts() {
        return associatedContactRepository.findAll().stream()
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
        AssociatedContact associatedContact = getAssociatedContactById(id);
        return mapToResponse(associatedContact);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedContactResponse> getAssociatedContactsByIdentifier(Long identifierId) {
        return associatedContactRepository.findByIdentifierId(identifierId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssociatedContactResponse> getAssociatedContactsByState(Long stateId) {
        return associatedContactRepository.findByStateId(stateId).stream()
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
        User user = userRepository.findById(associatedContact.getIdentifier().getUserId())
                .orElse(null);
        
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
} 