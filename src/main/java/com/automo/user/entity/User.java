package com.automo.user.entity;

import com.automo.auth.entity.Auth;
import com.automo.country.entity.Country;
import com.automo.model.AbstractModel;
import com.automo.organizationType.entity.OrganizationType;
import com.automo.province.entity.Province;
import com.automo.state.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_name", columnList = "name"),
    @Index(name = "idx_user_contacto", columnList = "contacto"),
    @Index(name = "idx_user_auth", columnList = "auth_id"),
    @Index(name = "idx_user_country", columnList = "country_id"),
    @Index(name = "idx_user_organization_type", columnList = "organization_type_id"),
    @Index(name = "idx_user_province", columnList = "province_id"),
    @Index(name = "idx_user_state", columnList = "state_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractModel {

    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;
    
    @Column
    private String img;
    
    @Column
    private String contacto;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id", nullable = false)
    @NotNull(message = "Auth is required")
    private Auth auth;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    @NotNull(message = "Country is required")
    private Country country;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_type_id", nullable = false)
    @NotNull(message = "Organization type is required")
    private OrganizationType organizationType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    @NotNull(message = "Province is required")
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private State state;
} 