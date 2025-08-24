package com.automo.area.repository;

import com.automo.area.entity.Area;
import com.automo.area.response.AreaResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    Optional<Area> findByArea(String area);
    
    List<Area> findByStateId(Long stateId);
    
    boolean existsByArea(String area);
    
    @Query("SELECT new com.automo.area.response.AreaResponse(a.id, a.area, a.description, a.state.id, a.state.state, a.createdAt, a.updatedAt) FROM Area a")
    List<AreaResponse> findAllResponse();
    
    @Query("SELECT new com.automo.area.response.AreaResponse(a.id, a.area, a.description, a.state.id, a.state.state, a.createdAt, a.updatedAt) FROM Area a WHERE a.id = :id")
    Optional<AreaResponse> findResponseById(Long id);
} 