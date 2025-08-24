package com.automo.user.repository;

import com.automo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    List<User> findByStateId(Long stateId);
    List<User> findByCountryId(Long countryId);
    List<User> findByOrganizationTypeId(Long organizationTypeId);
    List<User> findByProvinceId(Long provinceId);
    Optional<User> findByEmail(String email);
    Optional<User> findByAuthId(Long authId);
} 