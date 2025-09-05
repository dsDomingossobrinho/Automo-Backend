package com.automo.authRoles.service;

import com.automo.auth.entity.Auth;
import com.automo.auth.service.AuthService;
import com.automo.authRoles.dto.AuthRolesDto;
import com.automo.authRoles.entity.AuthRoles;
import com.automo.authRoles.repository.AuthRolesRepository;
import com.automo.authRoles.response.AuthRolesResponse;
import com.automo.role.entity.Role;
import com.automo.role.service.RoleService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthRolesServiceImpl implements AuthRolesService {

    private final AuthRolesRepository authRolesRepository;
    private final AuthService authService;
    private final RoleService roleService;
    private final StateService stateService;

    @Override
    public AuthRolesResponse createAuthRoles(AuthRolesDto authRolesDto) {
        // Verificar se a associação já existe
        if (authRolesRepository.existsByAuthIdAndRoleId(authRolesDto.authId(), authRolesDto.roleId())) {
            throw new RuntimeException("Auth already has this role assigned");
        }

        // Buscar entidades relacionadas
        Auth auth = authService.findById(authRolesDto.authId());
        
        Role role = roleService.findById(authRolesDto.roleId());
        
        State state = stateService.findById(authRolesDto.stateId());

        // Criar nova associação
        AuthRoles authRoles = new AuthRoles();
        authRoles.setAuth(auth);
        authRoles.setRole(role);
        authRoles.setState(state);

        AuthRoles savedAuthRoles = authRolesRepository.save(authRoles);
        return mapToResponse(savedAuthRoles);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthRolesResponse getAuthRolesById(Long id) {
        AuthRoles authRoles = authRolesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AuthRoles with ID " + id + " not found"));
        return mapToResponse(authRoles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthRolesResponse> getAllAuthRoles() {
        State eliminatedState = stateService.getEliminatedState();
        return authRolesRepository.findAll().stream()
                .filter(authRole -> authRole.getState() != null && !authRole.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AuthRolesResponse updateAuthRoles(Long id, AuthRolesDto authRolesDto) {
        AuthRoles authRoles = authRolesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AuthRoles with ID " + id + " not found"));

        // Verificar se a nova associação já existe (exceto para o registro atual)
        if (!authRoles.getAuth().getId().equals(authRolesDto.authId()) || 
            !authRoles.getRole().getId().equals(authRolesDto.roleId())) {
            if (authRolesRepository.existsByAuthIdAndRoleId(authRolesDto.authId(), authRolesDto.roleId())) {
                throw new RuntimeException("Auth already has this role assigned");
            }
        }

        // Buscar entidades relacionadas
        Auth auth = authService.findById(authRolesDto.authId());
        
        Role role = roleService.findById(authRolesDto.roleId());
        
        State state = stateService.findById(authRolesDto.stateId());

        // Atualizar associação
        authRoles.setAuth(auth);
        authRoles.setRole(role);
        authRoles.setState(state);

        AuthRoles updatedAuthRoles = authRolesRepository.save(authRoles);
        return mapToResponse(updatedAuthRoles);
    }

    @Override
    public void deleteAuthRoles(Long id) {
        AuthRoles authRoles = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        authRoles.setState(eliminatedState);
        
        authRolesRepository.save(authRoles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthRolesResponse> getAuthRolesByAuthId(Long authId) {
        return authRolesRepository.findByAuthId(authId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthRolesResponse> getAuthRolesByRoleId(Long roleId) {
        return authRolesRepository.findByRoleId(roleId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthRolesResponse> getAuthRolesByStateId(Long stateId) {
        return authRolesRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuthRolesResponse mapToResponse(AuthRoles authRoles) {
        return new AuthRolesResponse(
                authRoles.getId(),
                authRoles.getAuth().getId(),
                authRoles.getAuth().getEmail(),
                authRoles.getAuth().getUsername(),
                authRoles.getRole().getId(),
                authRoles.getRole().getRole(),
                authRoles.getState().getId(),
                authRoles.getState().getState(),
                authRoles.getCreatedAt(),
                authRoles.getUpdatedAt()
        );
    }

    @Override
    public AuthRoles findById(Long id) {
        return authRolesRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AuthRoles with ID " + id + " not found"));
    }

    @Override
    public AuthRoles findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        AuthRoles entity = authRolesRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AuthRoles with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("AuthRoles with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
    
    @Override
    public void createAuthRolesWithEntities(com.automo.auth.entity.Auth auth, com.automo.role.entity.Role role, com.automo.state.entity.State state) {
        // Verificar se a associação já existe
        if (authRolesRepository.existsByAuthIdAndRoleId(auth.getId(), role.getId())) {
            throw new RuntimeException("Auth already has this role assigned");
        }

        // Criar nova associação
        AuthRoles authRoles = new AuthRoles();
        authRoles.setAuth(auth);
        authRoles.setRole(role);
        authRoles.setState(state);

        authRolesRepository.save(authRoles);
    }
    
    @Override
    public List<AuthRoles> findByAuthId(Long authId) {
        return authRolesRepository.findByAuthId(authId);
    }
    
    @Override
    public AuthRoles save(AuthRoles authRoles) {
        return authRolesRepository.save(authRoles);
    }
    
    @Override
    public void delete(AuthRoles authRoles) {
        authRolesRepository.delete(authRoles);
    }
} 