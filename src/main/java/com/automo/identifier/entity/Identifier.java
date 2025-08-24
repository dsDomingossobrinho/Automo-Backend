package com.automo.identifier.entity;

import com.automo.identifierType.entity.IdentifierType;
import com.automo.model.AbstractModel;
import com.automo.state.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "identifiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Identifier extends AbstractModel {

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifier_type_id", nullable = false)
    @NotNull(message = "Identifier type is required")
    private IdentifierType identifierType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private State state;
} 