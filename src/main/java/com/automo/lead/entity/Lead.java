package com.automo.lead.entity;

import com.automo.identifier.entity.Identifier;
import com.automo.leadType.entity.LeadType;
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

@Entity
@Table(name = "leads", indexes = {
    @Index(name = "idx_lead_email", columnList = "email"),
    @Index(name = "idx_lead_name", columnList = "name"),
    @Index(name = "idx_lead_contact", columnList = "contact"),
    @Index(name = "idx_lead_zone", columnList = "zone"),
    @Index(name = "idx_lead_identifier", columnList = "identifier_id"),
    @Index(name = "idx_lead_type", columnList = "lead_type_id"),
    @Index(name = "idx_lead_state", columnList = "state_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lead extends AbstractModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifier_id", nullable = false)
    @NotNull(message = "Identifier is required")
    private Identifier identifier;
    
    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;
    
    @Column(nullable = false)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Column
    private String contact;
    
    @Column
    private String zone;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_type_id", nullable = false)
    @NotNull(message = "Lead type is required")
    private LeadType leadType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private State state;
} 