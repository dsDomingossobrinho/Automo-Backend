package com.automo.role.entity;

import com.automo.model.AbstractModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends AbstractModel {

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Role name is required")
    private String role;
    
    @Column
    private String description;
} 