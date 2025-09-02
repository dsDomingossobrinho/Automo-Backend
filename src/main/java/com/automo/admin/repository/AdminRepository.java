package com.automo.admin.repository;

import com.automo.admin.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    // Métodos existentes
    List<Admin> findByStateId(Long stateId);
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByAuthId(Long authId);
    
    // Métodos com JOIN FETCH para relacionamentos específicos
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.auth LEFT JOIN FETCH a.state WHERE a.email = :email")
    Optional<Admin> findByEmailWithAuthAndState(String email);
    
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.auth LEFT JOIN FETCH a.state WHERE a.auth.id = :authId")
    Optional<Admin> findByAuthIdWithAuthAndState(Long authId);
    
    // Método com JOIN FETCH para evitar LazyInitializationException
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.auth LEFT JOIN FETCH a.state")
    List<Admin> findAllWithAuthAndState();
    
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.auth LEFT JOIN FETCH a.state WHERE a.id = :id")
    Optional<Admin> findByIdWithAuthAndState(Long id);
    
    // Paginated search methods
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.auth LEFT JOIN FETCH a.state WHERE " +
           "(:search = '' OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.auth.username) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Admin> findBySearchCriteria(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.auth LEFT JOIN FETCH a.state WHERE " +
           "a.state.id = :stateId AND " +
           "(:search = '' OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.auth.username) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Admin> findByStateIdAndSearchCriteria(@Param("stateId") Long stateId, @Param("search") String search, Pageable pageable);
} 