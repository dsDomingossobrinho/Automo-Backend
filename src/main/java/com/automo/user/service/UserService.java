package com.automo.user.service;

import com.automo.user.dto.UserDto;
import com.automo.user.entity.User;
import com.automo.user.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserDto userDto);

    UserResponse updateUser(Long id, UserDto userDto);

    List<UserResponse> getAllUsers();

    User getUserById(Long id);

    UserResponse getUserByIdResponse(Long id);

    List<UserResponse> getUsersByState(Long stateId);

    List<UserResponse> getUsersByCountry(Long countryId);

    List<UserResponse> getUsersByOrganizationType(Long organizationTypeId);

    List<UserResponse> getUsersByProvince(Long provinceId);

    UserResponse getUserByEmail(String email);

    UserResponse getUserByAuthId(Long authId);

    void deleteUser(Long id);
} 