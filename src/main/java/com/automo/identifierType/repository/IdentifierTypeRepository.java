package com.automo.identifierType.repository;

import com.automo.identifierType.entity.IdentifierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdentifierTypeRepository extends JpaRepository<IdentifierType, Long> {

    Optional<IdentifierType> findByType(String type);
    
    boolean existsByType(String type);
} 