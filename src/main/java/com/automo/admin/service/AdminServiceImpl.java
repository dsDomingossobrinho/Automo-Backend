package com.automo.admin.service;

import com.automo.admin.dto.AdminDto;
import com.automo.admin.entity.Admin;
import com.automo.admin.repository.AdminRepository;
import com.automo.admin.response.AdminResponse;
import com.automo.auth.entity.Auth;
import com.automo.auth.service.AuthEntityCreationService;
import com.automo.model.dto.PaginationRequest;
import com.automo.model.service.BaseServiceImpl;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl extends BaseServiceImpl<Admin, AdminResponse, Long> implements AdminService {

    private final AdminRepository adminRepository;
    private final StateService stateService;
    private final AuthEntityCreationService authEntityCreationService;

    public AdminServiceImpl(AdminRepository adminRepository,
                           StateService stateService,
                           AuthEntityCreationService authEntityCreationService) {
        super(adminRepository);
        this.adminRepository = adminRepository;
        this.stateService = stateService;
        this.authEntityCreationService = authEntityCreationService;
    }

    @Override
    @Transactional
    public AdminResponse createAdmin(AdminDto adminDto) {
        // 1. PRIMEIRO: Criar o Admin (entidade principal)
        State state = stateService.findById(adminDto.stateId());
        
        Admin admin = new Admin();
        admin.setEmail(adminDto.email());
        admin.setName(adminDto.name());
        admin.setImg(adminDto.img());
        admin.setState(state);
        
        Admin savedAdmin = adminRepository.save(admin);
        
        // 2. SEGUNDO: Criar Auth completo usando AuthEntityCreationService
        Auth savedAuth = authEntityCreationService.createAuthForEntity(
                adminDto.email(),
                adminDto.name(),
                adminDto.password(),
                adminDto.contact(),
                adminDto.accountTypeId(),
                state,
                "ADMIN",
                "ADMIN"
        );
        
        // 3. TERCEIRO: Atualizar o Admin com a referência do Auth criado
        savedAdmin.setAuth(savedAuth);
        savedAdmin = adminRepository.save(savedAdmin);
        
        return mapToResponse(savedAdmin);
    }

    @Override
    @Transactional
    public AdminResponse updateAdmin(Long id, AdminDto adminDto) {
        Admin admin = this.getAdminById(id);
        State state = stateService.findById(adminDto.stateId());
        
        // Atualizar dados do Admin
        admin.setEmail(adminDto.email());
        admin.setName(adminDto.name());
        admin.setImg(adminDto.img());
        admin.setState(state);
        
        // Atualizar dados do Auth associado usando AuthEntityCreationService
        Auth auth = admin.getAuth();
        if (auth != null) {
            authEntityCreationService.updateAuthForEntity(
                    auth,
                    adminDto.email(),
                    adminDto.password(),
                    adminDto.contact(),
                    adminDto.accountTypeId(),
                    state
            );
        }
        
        Admin updatedAdmin = adminRepository.save(admin);
        return mapToResponse(updatedAdmin);
    }

    @Override
    public List<AdminResponse> getAllAdmins() {
        State eliminatedState = stateService.getEliminatedState();
        return adminRepository.findAllWithAuthAndState().stream()
                .filter(admin -> admin.getState() != null && !admin.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Admin getAdminById(Long id) {
        return adminRepository.findByIdWithAuthAndState(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin with ID " + id + " not found"));
    }

    @Override
    public AdminResponse getAdminByIdResponse(Long id) {
        Admin admin = this.getAdminById(id);
        return mapToResponse(admin);
    }

    @Override
    public AdminResponse getAdminByEmail(String email) {
        Admin admin = adminRepository.findByEmailWithAuthAndState(email)
                .orElseThrow(() -> new EntityNotFoundException("Admin with email " + email + " not found"));
        return mapToResponse(admin);
    }

    @Override
    public AdminResponse getAdminByAuthId(Long authId) {
        Admin admin = adminRepository.findByAuthIdWithAuthAndState(authId)
                .orElseThrow(() -> new EntityNotFoundException("Admin with auth ID " + authId + " not found"));
        return mapToResponse(admin);
    }

    @Override
    public void deleteAdmin(Long id) {
        Admin admin = this.getAdminById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        admin.setState(eliminatedState);
        
        adminRepository.save(admin);
    }

    @Override
    protected AdminResponse mapToResponse(Admin admin) {
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

    // Abstract methods implementation from BaseServiceImpl
    @Override
    protected Page<Admin> getEntitiesPage(PaginationRequest request, Pageable pageable) {
        return adminRepository.findBySearchCriteria(request.search(), pageable);
    }

    @Override
    protected Page<Admin> getActiveEntitiesPage(PaginationRequest request, Pageable pageable) {
        // For Admin, we don't have a specific active state concept, so return all
        return adminRepository.findBySearchCriteria(request.search(), pageable);
    }

    @Override
    protected Page<Admin> getEntitiesByStatePage(Long stateId, PaginationRequest request, Pageable pageable) {
        return adminRepository.findByStateIdAndSearchCriteria(stateId, request.search(), pageable);
    }

    @Override
    protected List<Admin> getActiveEntitiesList() {
        return adminRepository.findAllWithAuthAndState();
    }

    @Override
    protected List<Admin> getEntitiesByStateList(Long stateId) {
        return adminRepository.findByStateId(stateId);
    }

    @Override
    protected void deactivateEntityInternal(Admin entity) {
        // For Admin, we don't have a specific deactivation logic
        // This could be implemented as soft delete if needed
        throw new UnsupportedOperationException("Admin deactivation not implemented");
    }

    @Override
    protected boolean isEntityActiveInternal(Admin entity) {
        // For Admin, we consider all entities as active
        return true;
    }

    @Override
    protected long countActiveEntitiesInternal() {
        return adminRepository.count();
    }

    @Override
    protected long countEntitiesByStateInternal(Long stateId) {
        return adminRepository.findByStateId(stateId).size();
    }

    @Override
    protected void validateStateExists(Long stateId) {
        try {
            stateService.findById(stateId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("State with ID " + stateId + " not found");
        }
    }
    
    @Override
    public boolean isActiveAdminByAuthId(Long authId) {
        try {
            Optional<Admin> adminOptional = adminRepository.findByAuthId(authId);
            if (adminOptional.isEmpty()) {
                return false;
            }
            
            Admin admin = adminOptional.get();
            // Verificar se o admin está ativo (state_id = 1)
            return admin.getState() != null && admin.getState().getId().equals(1L);
        } catch (Exception e) {
            // Usar System.out.println ao invés de log que não está disponível
            System.err.println("Error checking admin status for auth ID: " + authId + " - " + e.getMessage());
            return false;
        }
    }
} 