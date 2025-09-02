package com.automo.associatedContact.repository;

import com.automo.associatedContact.entity.AssociatedContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssociatedContactRepository extends JpaRepository<AssociatedContact, Long> {
    
    /**
     * Find all associated contacts with identifier and state data eagerly loaded
     */
    @Query("SELECT ac FROM AssociatedContact ac LEFT JOIN FETCH ac.identifier LEFT JOIN FETCH ac.state")
    List<AssociatedContact> findAllWithIdentifierAndState();
    
    /**
     * Find associated contact by ID with identifier and state data eagerly loaded
     */
    @Query("SELECT ac FROM AssociatedContact ac LEFT JOIN FETCH ac.identifier LEFT JOIN FETCH ac.state WHERE ac.id = :id")
    Optional<AssociatedContact> findByIdWithIdentifierAndState(@Param("id") Long id);
    
    /**
     * Find associated contacts by identifier ID with identifier and state data eagerly loaded
     */
    @Query("SELECT ac FROM AssociatedContact ac LEFT JOIN FETCH ac.identifier LEFT JOIN FETCH ac.state WHERE ac.identifier.id = :identifierId")
    List<AssociatedContact> findByIdentifierIdWithIdentifierAndState(@Param("identifierId") Long identifierId);
    
    /**
     * Find associated contacts by state ID with identifier and state data eagerly loaded
     */
    @Query("SELECT ac FROM AssociatedContact ac LEFT JOIN FETCH ac.identifier LEFT JOIN FETCH ac.state WHERE ac.state.id = :stateId")
    List<AssociatedContact> findByStateIdWithIdentifierAndState(@Param("stateId") Long stateId);
    
    // Keep original methods for backward compatibility
    List<AssociatedContact> findByIdentifierId(Long identifierId);
    List<AssociatedContact> findByStateId(Long stateId);
} 