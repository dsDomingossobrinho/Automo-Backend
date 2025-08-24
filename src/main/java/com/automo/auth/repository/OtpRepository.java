package com.automo.auth.repository;

import com.automo.auth.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    
    Optional<Otp> findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
            String contact, String otpCode, String purpose, LocalDateTime now);
    
    @Modifying
    @Query("UPDATE Otp o SET o.used = true WHERE o.contact = :contact AND o.purpose = :purpose")
    void markAllAsUsedByContactAndPurpose(@Param("contact") String contact, @Param("purpose") String purpose);
    
    void deleteByExpiresAtBefore(LocalDateTime now);
} 