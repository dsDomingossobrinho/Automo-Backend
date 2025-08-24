package com.automo.state.entity;

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
@Table(name = "states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class State extends AbstractModel {

    @Column(nullable = false)
    @NotBlank(message = "State is required")
    private String state;
    
    @Column
    private String description;
} 