package com.automo.agent.repository;

import com.automo.agent.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    
    /**
     * Find all agents with their state data eagerly loaded to avoid LazyInitializationException
     */
    @Query("SELECT a FROM Agent a LEFT JOIN FETCH a.state")
    List<Agent> findAllWithState();
    
    /**
     * Find agent by ID with state data eagerly loaded
     */
    @Query("SELECT a FROM Agent a LEFT JOIN FETCH a.state WHERE a.id = :id")
    Optional<Agent> findByIdWithState(@Param("id") Long id);
    
    /**
     * Find agents by state ID with state data eagerly loaded
     */
    @Query("SELECT a FROM Agent a LEFT JOIN FETCH a.state WHERE a.state.id = :stateId")
    List<Agent> findByStateIdWithState(@Param("stateId") Long stateId);
    
    /**
     * Find agents by name containing (case insensitive) with state data eagerly loaded
     */
    @Query("SELECT a FROM Agent a LEFT JOIN FETCH a.state WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Agent> findByNameContainingIgnoreCaseWithState(@Param("name") String name);
    
    // Keep original methods for backward compatibility
    List<Agent> findByStateId(Long stateId);
    List<Agent> findByNameContainingIgnoreCase(String name);
} 