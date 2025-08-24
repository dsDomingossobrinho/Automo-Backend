package com.automo.user.service;

import com.automo.user.dto.UserDto;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
import com.automo.user.response.UserResponse;
import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.country.entity.Country;
import com.automo.country.repository.CountryRepository;
import com.automo.organizationType.entity.OrganizationType;
import com.automo.organizationType.repository.OrganizationTypeRepository;
import com.automo.province.entity.Province;
import com.automo.province.repository.ProvinceRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final CountryRepository countryRepository;
    private final OrganizationTypeRepository organizationTypeRepository;
    private final ProvinceRepository provinceRepository;
    private final StateRepository stateRepository;

    @Override
    public UserResponse createUser(UserDto userDto) {
        Auth auth = authRepository.findById(userDto.authId())
                .orElseThrow(() -> new EntityNotFoundException("Auth with ID " + userDto.authId() + " not found"));

        Country country = countryRepository.findById(userDto.countryId())
                .orElseThrow(() -> new EntityNotFoundException("Country with ID " + userDto.countryId() + " not found"));

        OrganizationType organizationType = organizationTypeRepository.findById(userDto.organizationTypeId())
                .orElseThrow(() -> new EntityNotFoundException("OrganizationType with ID " + userDto.organizationTypeId() + " not found"));

        State state = stateRepository.findById(userDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + userDto.stateId() + " not found"));

        User user = new User();
        user.setEmail(userDto.email());
        user.setName(userDto.name());
        user.setImg(userDto.img());
        user.setAuth(auth);
        user.setCountry(country);
        user.setOrganizationType(organizationType);
        user.setState(state);
        
        if (userDto.provinceId() != null) {
            Province province = provinceRepository.findById(userDto.provinceId())
                    .orElseThrow(() -> new EntityNotFoundException("Province with ID " + userDto.provinceId() + " not found"));
            user.setProvince(province);
        }
        
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserDto userDto) {
        User user = this.getUserById(id);
        
        Auth auth = authRepository.findById(userDto.authId())
                .orElseThrow(() -> new EntityNotFoundException("Auth with ID " + userDto.authId() + " not found"));

        Country country = countryRepository.findById(userDto.countryId())
                .orElseThrow(() -> new EntityNotFoundException("Country with ID " + userDto.countryId() + " not found"));

        OrganizationType organizationType = organizationTypeRepository.findById(userDto.organizationTypeId())
                .orElseThrow(() -> new EntityNotFoundException("OrganizationType with ID " + userDto.organizationTypeId() + " not found"));

        State state = stateRepository.findById(userDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + userDto.stateId() + " not found"));

        user.setEmail(userDto.email());
        user.setName(userDto.name());
        user.setImg(userDto.img());
        user.setAuth(auth);
        user.setCountry(country);
        user.setOrganizationType(organizationType);
        user.setState(state);
        
        if (userDto.provinceId() != null) {
            Province province = provinceRepository.findById(userDto.provinceId())
                    .orElseThrow(() -> new EntityNotFoundException("Province with ID " + userDto.provinceId() + " not found"));
            user.setProvince(province);
        } else {
            user.setProvince(null);
        }
        
        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
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
    public List<UserResponse> getUsersByState(Long stateId) {
        return userRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getUsersByCountry(Long countryId) {
        return userRepository.findByCountryId(countryId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getUsersByOrganizationType(Long organizationTypeId) {
        return userRepository.findByOrganizationTypeId(organizationTypeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getUsersByProvince(Long provinceId) {
        return userRepository.findByProvinceId(provinceId).stream()
                .map(this::mapToResponse)
                .toList();
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
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getImg(),
                user.getAuth().getId(),
                user.getAuth().getUsername(),
                user.getCountry().getId(),
                user.getCountry().getCountry(),
                user.getOrganizationType().getId(),
                user.getOrganizationType().getType(),
                user.getProvince() != null ? user.getProvince().getId() : null,
                user.getProvince() != null ? user.getProvince().getProvince() : null,
                user.getState().getId(),
                user.getState().getState(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
} 