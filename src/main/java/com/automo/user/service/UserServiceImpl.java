package com.automo.user.service;

import com.automo.user.dto.UserDto;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
import com.automo.user.response.UserResponse;
import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.accountType.service.AccountTypeService;
import com.automo.country.service.CountryService;
import com.automo.organizationType.service.OrganizationTypeService;
import com.automo.province.service.ProvinceService;
import com.automo.identifier.service.IdentifierService;
import com.automo.auth.service.AuthService;
import com.automo.authRoles.service.AuthRolesService;
import com.automo.role.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final StateService stateService;
    private final AccountTypeService accountTypeService;
    private final CountryService countryService;
    private final OrganizationTypeService organizationTypeService;
    private final ProvinceService provinceService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final IdentifierService identifierService;
    private final AuthRolesService authRolesService;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository,
                          AuthRepository authRepository,
                          StateService stateService,
                          AccountTypeService accountTypeService,
                          CountryService countryService,
                          OrganizationTypeService organizationTypeService,
                          ProvinceService provinceService,
                          PasswordEncoder passwordEncoder,
                          AuthService authService,
                          IdentifierService identifierService,
                          AuthRolesService authRolesService,
                          RoleService roleService) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
        this.stateService = stateService;
        this.accountTypeService = accountTypeService;
        this.countryService = countryService;
        this.organizationTypeService = organizationTypeService;
        this.provinceService = provinceService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.identifierService = identifierService;
        this.authRolesService = authRolesService;
        this.roleService = roleService;
    }

    @Override
    public UserResponse createUser(UserDto userDto) {
        // 1. PRIMEIRO: Criar o User (entidade principal)
        State state = stateService.findById(userDto.stateId());
        
        User user = new User();
        user.setEmail(userDto.email());
        user.setName(userDto.name());
        user.setImg(userDto.img());
        user.setContacto(userDto.contacto());
        user.setCountry(countryService.findById(userDto.countryId()));
        user.setOrganizationType(organizationTypeService.findById(userDto.organizationTypeId()));
        if (userDto.provinceId() != null) {
            user.setProvince(provinceService.findById(userDto.provinceId()));
        }
        user.setState(state);
        // Não definir auth ainda - será definido após criação do Auth
        
        User savedUser = userRepository.save(user);
        
        // 2. SEGUNDO: Criar o Auth (periférico) com username gerado automaticamente
        String uniqueUsername = authService.generateUniqueUsername(userDto.name());
        
        Auth auth = new Auth();
        auth.setEmail(userDto.email());
        auth.setUsername(uniqueUsername);
        auth.setPassword(passwordEncoder.encode(userDto.password()));
        auth.setContact(userDto.contacto());
        auth.setAccountType(accountTypeService.findById(userDto.accountTypeId()));
        auth.setState(state);
        
        Auth savedAuth = authRepository.save(auth);
        
        // 3. TERCEIRO: Atualizar o User com a referência do Auth criado
        savedUser.setAuth(savedAuth);
        savedUser = userRepository.save(savedUser);
        
        // 4. QUARTO: Criar os Identifiers
        identifierService.createIdentifierForEntity(savedAuth.getId(), "USER", state.getId());
        
        // 5. QUINTO: Atribuir role USER ao usuário criado
        try {
            com.automo.role.entity.Role userRole = roleService.findByRole("USER");
            authRolesService.createAuthRolesWithEntities(savedAuth, userRole, state);
        } catch (Exception e) {
            // Se não existir role USER, criar um usuário sem role por enquanto
            // Pode-se implementar criação automática da role USER aqui se necessário
        }
        
        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserDto userDto) {
        User user = this.getUserById(id);
        State state = stateService.findById(userDto.stateId());
        
        // Atualizar dados do User
        user.setEmail(userDto.email());
        user.setName(userDto.name());
        user.setImg(userDto.img());
        user.setContacto(userDto.contacto());
        user.setCountry(countryService.findById(userDto.countryId()));
        user.setOrganizationType(organizationTypeService.findById(userDto.organizationTypeId()));
        if (userDto.provinceId() != null) {
            user.setProvince(provinceService.findById(userDto.provinceId()));
        }
        user.setState(state);
        
        // Atualizar dados do Auth associado
        Auth auth = user.getAuth();
        if (auth != null) {
            auth.setEmail(userDto.email());
            // Username não é alterado durante update - mantém o original
            if (userDto.password() != null && !userDto.password().isEmpty()) {
                auth.setPassword(passwordEncoder.encode(userDto.password()));
            }
            auth.setContact(userDto.contacto());
            auth.setAccountType(accountTypeService.findById(userDto.accountTypeId()));
            auth.setState(state);
            authRepository.save(auth);
        }
        
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        State eliminatedState = stateService.getEliminatedState();
        return userRepository.findAll().stream()
                .filter(user -> user.getState() != null && !user.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public UserResponse getUserByIdResponse(Long id) {
        User user = this.getUserById(id);
        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));
        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserByAuthId(Long authId) {
        User user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new EntityNotFoundException("User with auth ID " + authId + " not found"));
        return mapToResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        user.setState(eliminatedState);
        
        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getImg(),
                null, // authId - não implementado ainda
                null, // authUsername - não implementado ainda
                null, // countryId - não implementado ainda
                null, // countryName - não implementado ainda
                null, // organizationTypeId - não implementado ainda
                null, // organizationTypeName - não implementado ainda
                null, // provinceId - não implementado ainda
                null, // provinceName - não implementado ainda
                null, // stateId - não implementado ainda
                null, // stateName - não implementado ainda
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public User findByIdAndStateId(Long id, Long stateId) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User with ID " + id + " not found"));
        
        // User has state relationship, check if entity's state matches required state
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("User with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 