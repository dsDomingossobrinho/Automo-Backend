package com.automo.state.repository;

import com.automo.state.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    Optional<State> findByState(String state);
    
    boolean existsByState(String state);
} 