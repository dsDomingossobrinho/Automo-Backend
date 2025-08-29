package com.automo.admin.repository;

import com.automo.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    // Métodos existentes
    List<Admin> findByStateId(Long stateId);
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByAuthId(Long authId);
} 