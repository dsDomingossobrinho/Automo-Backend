package com.automo.user.service;

import com.automo.user.dto.UserDto;
import com.automo.user.entity.User;
import com.automo.user.response.UserResponse;

import java.util.List;

public interface UserService {

    /**
     * Cria um novo usuário
     */
    UserResponse createUser(UserDto userDto);

    /**
     * Atualiza um usuário existente
     */
    UserResponse updateUser(Long id, UserDto userDto);

    /**
     * Obtém todos os usuários
     */
    List<UserResponse> getAllUsers();

    /**
     * Obtém usuário por ID
     */
    User getUserById(Long id);

    /**
     * Obtém usuário por ID com resposta DTO
     */
    UserResponse getUserByIdResponse(Long id);

    /**
     * Obtém usuário por email
     */
    UserResponse getUserByEmail(String email);

    /**
     * Obtém usuário por ID de autenticação
     */
    UserResponse getUserByAuthId(Long authId);

    /**
     * Deleta um usuário
     */
    void deleteUser(Long id);
} 