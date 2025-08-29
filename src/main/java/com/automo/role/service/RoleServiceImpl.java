package com.automo.role.service;

import com.automo.role.dto.RoleDto;
import com.automo.role.entity.Role;
import com.automo.role.repository.RoleRepository;
import com.automo.role.response.RoleResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponse createRole(RoleDto roleDto) {
        Role role = new Role();
        role.setRole(roleDto.role());
        role.setDescription(roleDto.description());
        
        Role savedRole = roleRepository.save(role);
        return mapToResponse(savedRole);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleDto roleDto) {
        Role role = this.getRoleById(id);
        
        role.setRole(roleDto.role());
        role.setDescription(roleDto.description());
        
        Role updatedRole = roleRepository.save(role);
        return mapToResponse(updatedRole);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role with ID " + id + " not found"));
    }

    @Override
    public RoleResponse getRoleByIdResponse(Long id) {
        Role role = this.getRoleById(id);
        return mapToResponse(role);
    }

    @Override
    public Role getRoleByRole(String role) {
        return roleRepository.findByRole(role)
                .orElseThrow(() -> new EntityNotFoundException("Role '" + role + "' not found"));
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Role with ID " + id + " not found");
        }
        roleRepository.deleteById(id);
    }

    private RoleResponse mapToResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getRole(),
                role.getDescription(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
} 