package com.automo.agent.entity;

import com.automo.model.AbstractModel;
import com.automo.state.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agent extends AbstractModel {

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;
    
    @Column
    private String description;
    
    @Column
    private String location;
    
    @Column
    private String restrictions;
    
    @Column
    private String activityFlow;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private State state;
} 