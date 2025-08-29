package com.automo.identifier.service;

import com.automo.identifier.dto.IdentifierDto;
import com.automo.identifier.entity.Identifier;
import com.automo.identifier.repository.IdentifierRepository;
import com.automo.identifier.response.IdentifierResponse;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.identifierType.repository.IdentifierTypeRepository;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdentifierServiceImpl implements IdentifierService {

    private final IdentifierRepository identifierRepository;
    private final UserRepository userRepository;
    private final IdentifierTypeRepository identifierTypeRepository;
    private final StateRepository stateRepository;

    @Override
    public IdentifierResponse createIdentifier(IdentifierDto identifierDto) {
        User user = userRepository.findById(identifierDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + identifierDto.userId() + " not found"));

        IdentifierType identifierType = identifierTypeRepository.findById(identifierDto.identifierTypeId())
                .orElseThrow(() -> new EntityNotFoundException("IdentifierType with ID " + identifierDto.identifierTypeId() + " not found"));

        State state = stateRepository.findById(identifierDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + identifierDto.stateId() + " not found"));

        Identifier identifier = new Identifier();
        identifier.setUserId(identifierDto.userId());
        identifier.setIdentifierType(identifierType);
        identifier.setState(state);
        
        Identifier savedIdentifier = identifierRepository.save(identifier);
        return mapToResponse(savedIdentifier, user);
    }

    @Override
    public IdentifierResponse updateIdentifier(Long id, IdentifierDto identifierDto) {
        Identifier identifier = this.getIdentifierById(id);
        
        User user = userRepository.findById(identifierDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + identifierDto.userId() + " not found"));

        IdentifierType identifierType = identifierTypeRepository.findById(identifierDto.identifierTypeId())
                .orElseThrow(() -> new EntityNotFoundException("IdentifierType with ID " + identifierDto.identifierTypeId() + " not found"));

        State state = stateRepository.findById(identifierDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + identifierDto.stateId() + " not found"));

        identifier.setUserId(identifierDto.userId());
        identifier.setIdentifierType(identifierType);
        identifier.setState(state);
        
        Identifier updatedIdentifier = identifierRepository.save(identifier);
        return mapToResponse(updatedIdentifier, user);
    }

    @Override
    public List<IdentifierResponse> getAllIdentifiers() {
        return identifierRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Identifier getIdentifierById(Long id) {
        return identifierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Identifier with ID " + id + " not found"));
    }

    @Override
    public IdentifierResponse getIdentifierByIdResponse(Long id) {
        Identifier identifier = this.getIdentifierById(id);
        return mapToResponse(identifier);
    }

    @Override
    public List<IdentifierResponse> getIdentifiersByState(Long stateId) {
        return identifierRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<IdentifierResponse> getIdentifiersByUser(Long userId) {
        return identifierRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<IdentifierResponse> getIdentifiersByType(Long identifierTypeId) {
        return identifierRepository.findByIdentifierTypeId(identifierTypeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteIdentifier(Long id) {
        if (!identifierRepository.existsById(id)) {
            throw new EntityNotFoundException("Identifier with ID " + id + " not found");
        }
        identifierRepository.deleteById(id);
    }

    @Override
    public void createIdentifierForEntity(Long userId, String entityType, Long stateId) {
        try {
            // Buscar tipo de identifier padrão para a entidade
            IdentifierType identifierType = identifierTypeRepository.findByType(entityType)
                    .orElseGet(() -> {
                        // Se não existir, criar um tipo padrão
                        IdentifierType defaultType = new IdentifierType();
                        defaultType.setType(entityType);
                        defaultType.setDescription(entityType + " identifier");
                        return identifierTypeRepository.save(defaultType);
                    });

            // Buscar o estado
            State state = stateRepository.findById(stateId)
                    .orElseThrow(() -> new EntityNotFoundException("State with ID " + stateId + " not found"));

            // Criar identifier
            Identifier identifier = new Identifier();
            identifier.setUserId(userId);
            identifier.setIdentifierType(identifierType);
            identifier.setState(state);

            identifierRepository.save(identifier);
        } catch (Exception e) {
            // Log do erro mas não falhar a criação da entidade principal
            System.err.println("Error creating identifier for " + entityType + " with userId " + userId + ": " + e.getMessage());
        }
    }

    private IdentifierResponse mapToResponse(Identifier identifier) {
        // Buscar o usuário para obter o nome
        User user = userRepository.findById(identifier.getUserId())
                .orElse(null);
        
        return new IdentifierResponse(
                identifier.getId(),
                identifier.getUserId(),
                user != null ? user.getName() : null,
                identifier.getIdentifierType().getId(),
                identifier.getIdentifierType().getType(),
                identifier.getState().getId(),
                identifier.getState().getState(),
                identifier.getCreatedAt(),
                identifier.getUpdatedAt()
        );
    }

    private IdentifierResponse mapToResponse(Identifier identifier, User user) {
        return new IdentifierResponse(
                identifier.getId(),
                identifier.getUserId(),
                user != null ? user.getName() : null,
                identifier.getIdentifierType().getId(),
                identifier.getIdentifierType().getType(),
                identifier.getState().getId(),
                identifier.getState().getState(),
                identifier.getCreatedAt(),
                identifier.getUpdatedAt()
        );
    }
} 