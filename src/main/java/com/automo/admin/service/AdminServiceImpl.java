package com.automo.admin.service;

import com.automo.admin.dto.AdminDto;
import com.automo.admin.entity.Admin;
import com.automo.admin.repository.AdminRepository;
import com.automo.admin.response.AdminResponse;
import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.accountType.service.AccountTypeService;
import com.automo.identifier.service.IdentifierService;
import com.automo.auth.service.AuthService;
import com.automo.authRoles.service.AuthRolesService;
import com.automo.role.service.RoleService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.automo.model.dto.PaginationRequest;
import com.automo.model.service.BaseServiceImpl;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl extends BaseServiceImpl<Admin, AdminResponse, Long> implements AdminService {

    private final AdminRepository adminRepository;
    private final AuthRepository authRepository;
    private final StateService stateService;
    private final AccountTypeService accountTypeService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final IdentifierService identifierService;
    private final AuthRolesService authRolesService;
    private final RoleService roleService;

    public AdminServiceImpl(AdminRepository adminRepository,
                           AuthRepository authRepository,
                           StateService stateService,
                           AccountTypeService accountTypeService,
                           PasswordEncoder passwordEncoder,
                           AuthService authService,
                           IdentifierService identifierService,
                           AuthRolesService authRolesService,
                           RoleService roleService) {
        super(adminRepository);
        this.adminRepository = adminRepository;
        this.authRepository = authRepository;
        this.stateService = stateService;
        this.accountTypeService = accountTypeService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.identifierService = identifierService;
        this.authRolesService = authRolesService;
        this.roleService = roleService;
    }

    @Override
    public AdminResponse createAdmin(AdminDto adminDto) {
        // 1. PRIMEIRO: Criar o Admin (entidade principal)
        State state = stateService.findById(adminDto.stateId());
        
        Admin admin = new Admin();
        admin.setEmail(adminDto.email());
        admin.setName(adminDto.name());
        admin.setImg(adminDto.img());
        admin.setState(state);
        // Não definir auth ainda - será definido após criação do Auth
        
        Admin savedAdmin = adminRepository.save(admin);
        
        // 2. SEGUNDO: Criar o Auth (periférico) com username gerado automaticamente
        String uniqueUsername = authService.generateUniqueUsername(adminDto.name());
        
        Auth auth = new Auth();
        auth.setEmail(adminDto.email());
        auth.setUsername(uniqueUsername);
        auth.setPassword(passwordEncoder.encode(adminDto.password()));
        auth.setContact(adminDto.contact());
        auth.setAccountType(accountTypeService.findById(adminDto.accountTypeId()));
        auth.setState(state);
        
        Auth savedAuth = authRepository.save(auth);
        
        // 3. TERCEIRO: Atualizar o Admin com a referência do Auth criado
        savedAdmin.setAuth(savedAuth);
        savedAdmin = adminRepository.save(savedAdmin);
        
        // 4. QUARTO: Criar os Identifiers
        identifierService.createIdentifierForEntity(savedAuth.getId(), "ADMIN", state.getId());
        
        // 5. QUINTO: Atribuir role ADMIN ao administrador criado
        try {
            com.automo.role.entity.Role adminRole = roleService.findByRole("ADMIN");
            authRolesService.createAuthRolesWithEntities(savedAuth, adminRole, state);
        } catch (Exception e) {
            // Se não existir role ADMIN, criar um admin sem role por enquanto
            // Pode-se implementar criação automática da role ADMIN aqui se necessário
        }
        
        return mapToResponse(savedAdmin);
    }

    @Override
    public AdminResponse updateAdmin(Long id, AdminDto adminDto) {
        Admin admin = this.getAdminById(id);
        State state = stateService.findById(adminDto.stateId());
        
        // Atualizar dados do Admin
        admin.setEmail(adminDto.email());
        admin.setName(adminDto.name());
        admin.setImg(adminDto.img());
        admin.setState(state);
        
        // Atualizar dados do Auth associado
        Auth auth = admin.getAuth();
        if (auth != null) {
            auth.setEmail(adminDto.email());
            // Username não é alterado durante update - mantém o original
            if (adminDto.password() != null && !adminDto.password().isEmpty()) {
                auth.setPassword(passwordEncoder.encode(adminDto.password()));
            }
            auth.setContact(adminDto.contact());
            auth.setAccountType(accountTypeService.findById(adminDto.accountTypeId()));
            auth.setState(state);
            authRepository.save(auth);
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
} 