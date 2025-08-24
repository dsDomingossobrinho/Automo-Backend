package com.automo.province.repository;

import com.automo.province.entity.Province;
import com.automo.province.response.ProvinceResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {

    Optional<Province> findByProvince(String province);
    
    List<Province> findByCountryId(Long countryId);
    
    List<Province> findByStateId(Long stateId);
    
    boolean existsByProvince(String province);
    
    @Query("SELECT new com.automo.province.response.ProvinceResponse(p.id, p.province, p.country.id, p.country.country, p.state.id, p.state.state, p.createdAt, p.updatedAt) FROM Province p")
    List<ProvinceResponse> findAllResponse();
    
    @Query("SELECT new com.automo.province.response.ProvinceResponse(p.id, p.province, p.country.id, p.country.country, p.state.id, p.state.state, p.createdAt, p.updatedAt) FROM Province p WHERE p.id = :id")
    Optional<ProvinceResponse> findResponseById(Long id);
    
    @Query("SELECT new com.automo.province.response.ProvinceResponse(p.id, p.province, p.country.id, p.country.country, p.state.id, p.state.state, p.createdAt, p.updatedAt) FROM Province p WHERE p.country.id = :countryId")
    List<ProvinceResponse> findResponseByCountryId(Long countryId);
} 