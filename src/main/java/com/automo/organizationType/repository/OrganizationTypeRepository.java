package com.automo.organizationType.repository;

import com.automo.organizationType.entity.OrganizationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationTypeRepository extends JpaRepository<OrganizationType, Long> {

    Optional<OrganizationType> findByType(String type);
    
    boolean existsByType(String type);
} 