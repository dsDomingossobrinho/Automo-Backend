package com.automo.auth.repository;

import com.automo.auth.entity.Auth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    
    // Métodos existentes
    Optional<Auth> findByEmail(String email);
    
    Optional<Auth> findByUsername(String username);
    
    Optional<Auth> findByContact(String contact);
    
    @Query("SELECT a FROM Auth a WHERE a.email = :emailOrContact OR a.contact = :emailOrContact")
    Optional<Auth> findByEmailOrContact(@Param("emailOrContact") String emailOrContact);
    
    @Query("SELECT a FROM Auth a WHERE a.email = :emailOrContact OR a.username = :emailOrContact OR a.contact = :emailOrContact")
    Optional<Auth> findByEmailOrUsernameOrContact(@Param("emailOrContact") String emailOrContact);
    
    @Query("SELECT a FROM Auth a LEFT JOIN FETCH a.accountType WHERE a.email = :emailOrContact OR a.contact = :emailOrContact")
    Optional<Auth> findByEmailOrContactWithAccountType(@Param("emailOrContact") String emailOrContact);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByContact(String contact);
    
    // Métodos para busca paginada com filtros
    @Query("""
        SELECT DISTINCT a FROM Auth a 
        LEFT JOIN FETCH a.accountType at
        LEFT JOIN FETCH a.authRoles ar
        LEFT JOIN FETCH ar.role r
        LEFT JOIN FETCH a.state s
        WHERE a.accountType.id = 1 
        AND a.state.state != 'ELIMINATED'
        AND (:search IS NULL OR :search = '' OR 
             LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(a.contact) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Auth> findAllAdminsPaged(@Param("search") String search, Pageable pageable);
    
    @Query("""
        SELECT DISTINCT a FROM Auth a 
        LEFT JOIN FETCH a.accountType at
        LEFT JOIN FETCH a.authRoles ar
        LEFT JOIN FETCH ar.role r
        LEFT JOIN FETCH a.state s
        WHERE a.state.state != 'ELIMINATED'
        AND (:search IS NULL OR :search = '' OR 
             LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(a.contact) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Auth> findAllUsersPaged(@Param("search") String search, Pageable pageable);
    
    // Contar admins com filtro de pesquisa
    @Query("""
        SELECT COUNT(DISTINCT a) FROM Auth a 
        WHERE a.accountType.id = 1 
        AND a.state.state != 'ELIMINATED'
        AND (:search IS NULL OR :search = '' OR 
             LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(a.contact) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    long countAdminsPaged(@Param("search") String search);
    
    // Contar usuários com filtro de pesquisa
    @Query("""
        SELECT COUNT(DISTINCT a) FROM Auth a 
        WHERE a.state.state != 'ELIMINATED'
        AND (:search IS NULL OR :search = '' OR 
             LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
             LOWER(a.contact) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    long countUsersPaged(@Param("search") String search);
    
    // === MÉTODOS DE ESTATÍSTICAS ===
    
    /**
     * Conta total de usuários registrados (excluindo eliminados)
     */
    @Query("SELECT COUNT(a) FROM Auth a WHERE a.state.state != 'ELIMINATED'")
    long countTotalUsersRegistered();
    
    /**
     * Conta total de usuários ativos
     */
    @Query("SELECT COUNT(a) FROM Auth a WHERE a.state.state = 'ACTIVE'")
    long countTotalActiveUsers();
    
    /**
     * Conta total de usuários inativos
     */
    @Query("SELECT COUNT(a) FROM Auth a WHERE a.state.state = 'INACTIVE'")
    long countTotalInactiveUsers();
    
    /**
     * Busca usuários por tipo de conta
     */
    @Query("SELECT a FROM Auth a WHERE a.accountType.type = :type")
    List<Auth> findByAccountType_Type(@Param("type") String type);
} 