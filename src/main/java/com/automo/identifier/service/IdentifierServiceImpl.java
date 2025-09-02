package com.automo.identifier.service;

import com.automo.identifier.dto.IdentifierDto;
import com.automo.identifier.entity.Identifier;
import com.automo.identifier.repository.IdentifierRepository;
import com.automo.identifier.response.IdentifierResponse;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.identifierType.service.IdentifierTypeService;
import com.automo.user.entity.User;
import com.automo.user.service.UserService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IdentifierServiceImpl implements IdentifierService {

    private final IdentifierRepository identifierRepository;
    private final UserService userService;
    private final IdentifierTypeService identifierTypeService;
    private final StateService stateService;

    public IdentifierServiceImpl(IdentifierRepository identifierRepository,
                                 @Lazy UserService userService,
                                 IdentifierTypeService identifierTypeService,
                                 StateService stateService) {
        this.identifierRepository = identifierRepository;
        this.userService = userService;
        this.identifierTypeService = identifierTypeService;
        this.stateService = stateService;
    }

    @Override
    public IdentifierResponse createIdentifier(IdentifierDto identifierDto) {
        User user = userService.findById(identifierDto.userId());

        IdentifierType identifierType = identifierTypeService.findById(identifierDto.identifierTypeId());

        State state = stateService.findById(identifierDto.stateId());

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
        
        User user = userService.findById(identifierDto.userId());

        IdentifierType identifierType = identifierTypeService.findById(identifierDto.identifierTypeId());

        State state = stateService.findById(identifierDto.stateId());

        identifier.setUserId(identifierDto.userId());
        identifier.setIdentifierType(identifierType);
        identifier.setState(state);
        
        Identifier updatedIdentifier = identifierRepository.save(identifier);
        return mapToResponse(updatedIdentifier, user);
    }

    @Override
    public List<IdentifierResponse> getAllIdentifiers() {
        State eliminatedState = stateService.getEliminatedState();
        return identifierRepository.findAll().stream()
                .filter(identifier -> identifier.getState() != null && !identifier.getState().getId().equals(eliminatedState.getId()))
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
        Identifier identifier = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        identifier.setState(eliminatedState);
        
        identifierRepository.save(identifier);
    }

    @Override
    public void createIdentifierForEntity(Long userId, String entityType, Long stateId) {
        try {
            // Buscar tipo de identifier padrão para a entidade
            IdentifierType identifierType = identifierTypeService.findByType(entityType)
                    .orElseGet(() -> {
                        // Se não existir, criar um tipo padrão via service
                        return identifierTypeService.createDefaultIdentifierType(entityType, entityType + " identifier");
                    });

            // Buscar o estado
            State state = stateService.findById(stateId);

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
        User user = null;
        try {
            user = userService.findById(identifier.getUserId());
        } catch (Exception e) {
            // User not found, keep null
        }
        
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

    @Override
    public Identifier findById(Long id) {
        return identifierRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Identifier with ID " + id + " not found"));
    }

    @Override
    public Identifier findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Identifier entity = identifierRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Identifier with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Identifier with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 