package com.automo.accountType.entity;

import com.automo.model.AbstractModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountType extends AbstractModel {

    @Column(nullable = false)
    @NotBlank(message = "Type is required")
    private String type;
    
    @Column
    private String description;
} 