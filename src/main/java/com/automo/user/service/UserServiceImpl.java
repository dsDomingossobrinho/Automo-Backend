package com.automo.user.service;

import com.automo.user.dto.UserDto;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
import com.automo.user.response.UserResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StateService stateService;

    @Override
    public UserResponse createUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.name());
        user.setEmail(userDto.email());
        user.setImg(userDto.img());
        
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserDto userDto) {
        User user = this.getUserById(id);
        
        user.setName(userDto.name());
        user.setEmail(userDto.email());
        user.setImg(userDto.img());
        
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