package com.automo.leadType.repository;

import com.automo.leadType.entity.LeadType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeadTypeRepository extends JpaRepository<LeadType, Long> {

    Optional<LeadType> findByType(String type);
    
    boolean existsByType(String type);
} 