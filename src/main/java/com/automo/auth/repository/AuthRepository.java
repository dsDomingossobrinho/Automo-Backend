package com.automo.auth.repository;

import com.automo.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    
    Optional<Auth> findByEmail(String email);
    
    Optional<Auth> findByUsername(String username);
    
    Optional<Auth> findByContact(String contact);
    
    @Query("SELECT a FROM Auth a WHERE a.email = :emailOrContact OR a.contact = :emailOrContact")
    Optional<Auth> findByEmailOrContact(@Param("emailOrContact") String emailOrContact);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByContact(String contact);
} 