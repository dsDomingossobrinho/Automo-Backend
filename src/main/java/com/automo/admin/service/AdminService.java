package com.automo.admin.service;

import com.automo.admin.dto.AdminDto;
import com.automo.admin.entity.Admin;
import com.automo.admin.response.AdminResponse;

import java.util.List;

public interface AdminService {

    /**
     * Cria um novo admin
     */
    AdminResponse createAdmin(AdminDto adminDto);

    /**
     * Atualiza um admin existente
     */
    AdminResponse updateAdmin(Long id, AdminDto adminDto);

    /**
     * Obtém todos os admins
     */
    List<AdminResponse> getAllAdmins();

    /**
     * Obtém admin por ID
     */
    Admin getAdminById(Long id);

    /**
     * Obtém admin por ID com resposta DTO
     */
    AdminResponse getAdminByIdResponse(Long id);

    /**
     * Obtém admin por email
     */
    AdminResponse getAdminByEmail(String email);

    /**
     * Obtém admin por ID de autenticação
     */
    AdminResponse getAdminByAuthId(Long authId);

    /**
     * Deleta um admin
     */
    void deleteAdmin(Long id);
} 