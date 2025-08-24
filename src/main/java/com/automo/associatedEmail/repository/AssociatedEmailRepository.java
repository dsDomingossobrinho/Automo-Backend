package com.automo.associatedEmail.repository;

import com.automo.associatedEmail.entity.AssociatedEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssociatedEmailRepository extends JpaRepository<AssociatedEmail, Long> {
    
    List<AssociatedEmail> findByIdentifierId(Long identifierId);
    List<AssociatedEmail> findByStateId(Long stateId);
    Optional<AssociatedEmail> findByEmail(String email);
} 