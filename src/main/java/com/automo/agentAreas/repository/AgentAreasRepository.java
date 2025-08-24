package com.automo.agentAreas.repository;

import com.automo.agentAreas.entity.AgentAreas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentAreasRepository extends JpaRepository<AgentAreas, Long> {
    
    List<AgentAreas> findByAgentId(Long agentId);
    
    List<AgentAreas> findByAreaId(Long areaId);
    
    List<AgentAreas> findByStateId(Long stateId);
    
    boolean existsByAgentIdAndAreaId(Long agentId, Long areaId);
} 