package com.automo.accountType.repository;

import com.automo.accountType.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {

    Optional<AccountType> findByType(String type);
    
    boolean existsByType(String type);
} 