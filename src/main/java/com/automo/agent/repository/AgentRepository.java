package com.automo.agent.repository;

import com.automo.agent.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    
    List<Agent> findByStateId(Long stateId);
    List<Agent> findByNameContainingIgnoreCase(String name);
} 