package com.automo.lead.repository;

import com.automo.lead.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    List<Lead> findByStateId(Long stateId);
    List<Lead> findByLeadTypeId(Long leadTypeId);
    List<Lead> findByIdentifierId(Long identifierId);
} 