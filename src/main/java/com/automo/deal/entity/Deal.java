package com.automo.deal.entity;

import com.automo.identifier.entity.Identifier;
import com.automo.lead.entity.Lead;
import com.automo.model.AbstractModel;
import com.automo.promotion.entity.Promotion;
import com.automo.state.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "deals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Deal extends AbstractModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifier_id", nullable = false)
    @NotNull(message = "Identifier is required")
    private Identifier identifier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    @NotNull(message = "Lead is required")
    private Lead lead;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
    
    @Column(nullable = false)
    @NotNull(message = "Total is required")
    @Positive(message = "Total must be positive")
    private BigDecimal total;
    
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
    
    @Column(name = "message_count", nullable = false)
    @NotNull(message = "Message count is required")
    @Positive(message = "Message count must be positive")
    private Integer messageCount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private State state;
} 