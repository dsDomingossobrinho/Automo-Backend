package com.automo.country.repository;

import com.automo.country.entity.Country;
import com.automo.country.response.CountryResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByCountry(String country);
    
    List<Country> findByStateId(Long stateId);
    
    boolean existsByCountry(String country);
    
    @Query("SELECT new com.automo.country.response.CountryResponse(c.id, c.country, c.numberDigits, c.indicative, c.state.id, c.state.state, c.createdAt, c.updatedAt) FROM Country c")
    List<CountryResponse> findAllResponse();
    
    @Query("SELECT new com.automo.country.response.CountryResponse(c.id, c.country, c.numberDigits, c.indicative, c.state.id, c.state.state, c.createdAt, c.updatedAt) FROM Country c WHERE c.id = :id")
    Optional<CountryResponse> findResponseById(Long id);
} 