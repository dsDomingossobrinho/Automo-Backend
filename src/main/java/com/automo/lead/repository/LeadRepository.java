package com.automo.lead.repository;

import com.automo.lead.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    List<Lead> findByStateId(Long stateId);
    List<Lead> findByLeadTypeId(Long leadTypeId);
    List<Lead> findByIdentifierId(Long identifierId);
    
    // === MÉTODOS DE ESTATÍSTICAS DE AGENTE ===
    
    /**
     * Conta leads captados por agente (através do identifier.userId)
     */
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.identifier.userId = :agentId AND l.state.state != 'ELIMINATED'")
    long countLeadsCapturedByAgent(@Param("agentId") Long agentId);
    
    /**
     * Conta leads ativos captados por agente
     */
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.identifier.userId = :agentId AND l.state.state = 'ACTIVE'")
    long countActiveLeadsCapturedByAgent(@Param("agentId") Long agentId);
} 