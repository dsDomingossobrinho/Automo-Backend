package com.automo.identifier.repository;

import com.automo.identifier.entity.Identifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdentifierRepository extends JpaRepository<Identifier, Long> {
    
    List<Identifier> findByStateId(Long stateId);
    List<Identifier> findByUserId(Long userId);
    List<Identifier> findByIdentifierTypeId(Long identifierTypeId);
} 