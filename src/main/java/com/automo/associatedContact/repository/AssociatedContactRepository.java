package com.automo.associatedContact.repository;

import com.automo.associatedContact.entity.AssociatedContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociatedContactRepository extends JpaRepository<AssociatedContact, Long> {
    
    List<AssociatedContact> findByIdentifierId(Long identifierId);
    List<AssociatedContact> findByStateId(Long stateId);
} 