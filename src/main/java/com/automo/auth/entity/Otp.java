package com.automo.auth.entity;

import com.automo.model.AbstractModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Otp extends AbstractModel {

    @Column(nullable = false)
    private String contact; // Email ou telefone
    
    @Column(nullable = false)
    private String contactType; // EMAIL ou PHONE
    
    @Column(nullable = false)
    private String otpCode;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used = false;
    
    @Column(nullable = false)
    private String purpose; // LOGIN, RESET_PASSWORD, etc.
} 