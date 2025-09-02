package com.automo.agentAreas.repository;

import com.automo.agentAreas.entity.AgentAreas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentAreasRepository extends JpaRepository<AgentAreas, Long> {
    
    /**
     * Find all agent areas with agent, area and state data eagerly loaded
     */
    @Query("SELECT aa FROM AgentAreas aa LEFT JOIN FETCH aa.agent LEFT JOIN FETCH aa.area LEFT JOIN FETCH aa.state")
    List<AgentAreas> findAllWithAgentAreaAndState();
    
    /**
     * Find agent areas by ID with agent, area and state data eagerly loaded
     */
    @Query("SELECT aa FROM AgentAreas aa LEFT JOIN FETCH aa.agent LEFT JOIN FETCH aa.area LEFT JOIN FETCH aa.state WHERE aa.id = :id")
    Optional<AgentAreas> findByIdWithAgentAreaAndState(@Param("id") Long id);
    
    /**
     * Find agent areas by agent ID with agent, area and state data eagerly loaded
     */
    @Query("SELECT aa FROM AgentAreas aa LEFT JOIN FETCH aa.agent LEFT JOIN FETCH aa.area LEFT JOIN FETCH aa.state WHERE aa.agent.id = :agentId")
    List<AgentAreas> findByAgentIdWithAgentAreaAndState(@Param("agentId") Long agentId);
    
    /**
     * Find agent areas by area ID with agent, area and state data eagerly loaded
     */
    @Query("SELECT aa FROM AgentAreas aa LEFT JOIN FETCH aa.agent LEFT JOIN FETCH aa.area LEFT JOIN FETCH aa.state WHERE aa.area.id = :areaId")
    List<AgentAreas> findByAreaIdWithAgentAreaAndState(@Param("areaId") Long areaId);
    
    /**
     * Find agent areas by state ID with agent, area and state data eagerly loaded
     */
    @Query("SELECT aa FROM AgentAreas aa LEFT JOIN FETCH aa.agent LEFT JOIN FETCH aa.area LEFT JOIN FETCH aa.state WHERE aa.state.id = :stateId")
    List<AgentAreas> findByStateIdWithAgentAreaAndState(@Param("stateId") Long stateId);
    
    // Keep original methods for backward compatibility
    List<AgentAreas> findByAgentId(Long agentId);
    List<AgentAreas> findByAreaId(Long areaId);
    List<AgentAreas> findByStateId(Long stateId);
    boolean existsByAgentIdAndAreaId(Long agentId, Long areaId);
} 