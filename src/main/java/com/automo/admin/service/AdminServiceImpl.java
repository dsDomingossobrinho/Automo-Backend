package com.automo.admin.service;

import com.automo.admin.dto.AdminDto;
import com.automo.admin.entity.Admin;
import com.automo.admin.repository.AdminRepository;
import com.automo.admin.response.AdminResponse;
import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AuthRepository authRepository;
    private final StateRepository stateRepository;

    @Override
    public AdminResponse createAdmin(AdminDto adminDto) {
        Auth auth = authRepository.findById(adminDto.authId())
                .orElseThrow(() -> new EntityNotFoundException("Auth with ID " + adminDto.authId() + " not found"));

        State state = stateRepository.findById(adminDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + adminDto.stateId() + " not found"));

        Admin admin = new Admin();
        admin.setEmail(adminDto.email());
        admin.setName(adminDto.name());
        admin.setImg(adminDto.img());
        admin.setAuth(auth);
        admin.setState(state);
        
        Admin savedAdmin = adminRepository.save(admin);
        return mapToResponse(savedAdmin);
    }

    @Override
    public AdminResponse updateAdmin(Long id, AdminDto adminDto) {
        Admin admin = this.getAdminById(id);
        
        Auth auth = authRepository.findById(adminDto.authId())
                .orElseThrow(() -> new EntityNotFoundException("Auth with ID " + adminDto.authId() + " not found"));

        State state = stateRepository.findById(adminDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + adminDto.stateId() + " not found"));

        admin.setEmail(adminDto.email());
        admin.setName(adminDto.name());
        admin.setImg(adminDto.img());
        admin.setAuth(auth);
        admin.setState(state);
        
        Admin updatedAdmin = adminRepository.save(admin);
        return mapToResponse(updatedAdmin);
    }

    @Override
    public List<AdminResponse> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin with ID " + id + " not found"));
    }

    @Override
    public AdminResponse getAdminByIdResponse(Long id) {
        Admin admin = this.getAdminById(id);
        return mapToResponse(admin);
    }

    @Override
    public List<AdminResponse> getAdminsByState(Long stateId) {
        return adminRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AdminResponse getAdminByEmail(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Admin with email " + email + " not found"));
        return mapToResponse(admin);
    }

    @Override
    public AdminResponse getAdminByAuthId(Long authId) {
        Admin admin = adminRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("Admin with auth ID " + authId + " not found"));
        return mapToResponse(admin);
    }

    @Override
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new EntityNotFoundException("Admin with ID " + id + " not found");
        }
        adminRepository.deleteById(id);
    }

    private AdminResponse mapToResponse(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getEmail(),
                admin.getName(),
                admin.getImg(),
                admin.getAuth().getId(),
                admin.getAuth().getUsername(),
                admin.getState().getId(),
                admin.getState().getState(),
                admin.getCreatedAt(),
                admin.getUpdatedAt()
        );
    }
} 