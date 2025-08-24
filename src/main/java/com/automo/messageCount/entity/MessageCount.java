package com.automo.messageCount.entity;

import com.automo.lead.entity.Lead;
import com.automo.model.AbstractModel;
import com.automo.state.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "message_counts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageCount extends AbstractModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    @NotNull(message = "Lead is required")
    private Lead lead;
    
    @Column(name = "message_count", nullable = false)
    @NotNull(message = "Message count is required")
    @Positive(message = "Message count must be positive")
    private Integer messageCount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private State state;
} 