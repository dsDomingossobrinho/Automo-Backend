package com.automo.admin.service;

import com.automo.admin.dto.AdminDto;
import com.automo.admin.entity.Admin;
import com.automo.admin.response.AdminResponse;

import java.util.List;

public interface AdminService {

    AdminResponse createAdmin(AdminDto adminDto);

    AdminResponse updateAdmin(Long id, AdminDto adminDto);

    List<AdminResponse> getAllAdmins();

    Admin getAdminById(Long id);

    AdminResponse getAdminByIdResponse(Long id);

    List<AdminResponse> getAdminsByState(Long stateId);

    AdminResponse getAdminByEmail(String email);

    AdminResponse getAdminByAuthId(Long authId);

    void deleteAdmin(Long id);
} 