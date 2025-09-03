package com.automo.user.service;

import com.automo.user.dto.UserDto;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
import com.automo.user.response.UserResponse;
import com.automo.auth.entity.Auth;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.country.service.CountryService;
import com.automo.organizationType.service.OrganizationTypeService;
import com.automo.province.service.ProvinceService;
import com.automo.auth.service.AuthEntityCreationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StateService stateService;
    private final CountryService countryService;
    private final OrganizationTypeService organizationTypeService;
    private final ProvinceService provinceService;
    private final AuthEntityCreationService authEntityCreationService;

    public UserServiceImpl(UserRepository userRepository,
                          StateService stateService,
                          CountryService countryService,
                          OrganizationTypeService organizationTypeService,
                          ProvinceService provinceService,
                          AuthEntityCreationService authEntityCreationService) {
        this.userRepository = userRepository;
        this.stateService = stateService;
        this.countryService = countryService;
        this.organizationTypeService = organizationTypeService;
        this.provinceService = provinceService;
        this.authEntityCreationService = authEntityCreationService;
    }

    @Override
    @Transactional
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
        
        User savedUser = userRepository.save(user);
        
        // 2. SEGUNDO: Criar Auth completo usando AuthEntityCreationService
        Auth savedAuth = authEntityCreationService.createAuthForEntity(
                userDto.email(),
                userDto.name(), 
                userDto.password(),
                userDto.contacto(),
                userDto.accountTypeId(),
                state,
                "USER",
                "USER"
        );
        
        // 3. TERCEIRO: Atualizar o User com a referência do Auth criado
        savedUser.setAuth(savedAuth);
        savedUser = userRepository.save(savedUser);
        
        return mapToResponse(savedUser);
    }

    @Override
    @Transactional
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
        
        // Atualizar dados do Auth associado usando AuthEntityCreationService
        Auth auth = user.getAuth();
        if (auth != null) {
            authEntityCreationService.updateAuthForEntity(
                    auth,
                    userDto.email(),
                    userDto.password(),
                    userDto.contacto(),
                    userDto.accountTypeId(),
                    state
            );
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
    @Transactional
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