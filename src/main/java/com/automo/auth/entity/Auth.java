package com.automo.auth.entity;

import com.automo.accountType.entity.AccountType;
import com.automo.authRoles.entity.AuthRoles;
import com.automo.model.AbstractModel;
import com.automo.state.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auth", indexes = {
    @Index(name = "idx_auth_email", columnList = "email"),
    @Index(name = "idx_auth_username", columnList = "username"),
    @Index(name = "idx_auth_contact", columnList = "contact"),
    @Index(name = "idx_auth_account_type", columnList = "account_type_id"),
    @Index(name = "idx_auth_state", columnList = "state_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auth extends AbstractModel {

    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Column
    private String contact;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username is required")
    private String username;
    
    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    private String password;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_type_id", nullable = false)
    @NotNull(message = "Account type is required")
    private AccountType accountType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private State state;

    @OneToMany(mappedBy = "auth", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuthRoles> authRoles = new ArrayList<>();
} 