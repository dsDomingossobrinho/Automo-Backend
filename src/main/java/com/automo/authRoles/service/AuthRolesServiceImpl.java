package com.automo.authRoles.service;

import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.authRoles.dto.AuthRolesDto;
import com.automo.authRoles.entity.AuthRoles;
import com.automo.authRoles.repository.AuthRolesRepository;
import com.automo.authRoles.response.AuthRolesResponse;
import com.automo.role.entity.Role;
import com.automo.role.repository.RoleRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
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
    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final StateRepository stateRepository;

    @Override
    public AuthRolesResponse createAuthRoles(AuthRolesDto authRolesDto) {
        // Verificar se a associação já existe
        if (authRolesRepository.existsByAuthIdAndRoleId(authRolesDto.authId(), authRolesDto.roleId())) {
            throw new RuntimeException("Auth already has this role assigned");
        }

        // Buscar entidades relacionadas
        Auth auth = authRepository.findById(authRolesDto.authId())
                .orElseThrow(() -> new EntityNotFoundException("Auth with ID " + authRolesDto.authId() + " not found"));
        
        Role role = roleRepository.findById(authRolesDto.roleId())
                .orElseThrow(() -> new EntityNotFoundException("Role with ID " + authRolesDto.roleId() + " not found"));
        
        State state = stateRepository.findById(authRolesDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + authRolesDto.stateId() + " not found"));

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
        return authRolesRepository.findAll().stream()
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
        Auth auth = authRepository.findById(authRolesDto.authId())
                .orElseThrow(() -> new EntityNotFoundException("Auth with ID " + authRolesDto.authId() + " not found"));
        
        Role role = roleRepository.findById(authRolesDto.roleId())
                .orElseThrow(() -> new EntityNotFoundException("Role with ID " + authRolesDto.roleId() + " not found"));
        
        State state = stateRepository.findById(authRolesDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + authRolesDto.stateId() + " not found"));

        // Atualizar associação
        authRoles.setAuth(auth);
        authRoles.setRole(role);
        authRoles.setState(state);

        AuthRoles updatedAuthRoles = authRolesRepository.save(authRoles);
        return mapToResponse(updatedAuthRoles);
    }

    @Override
    public void deleteAuthRoles(Long id) {
        if (!authRolesRepository.existsById(id)) {
            throw new EntityNotFoundException("AuthRoles with ID " + id + " not found");
        }
        authRolesRepository.deleteById(id);
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
} 