package com.automo.payment.entity;

import com.automo.model.AbstractModel;
import com.automo.paymentType.entity.PaymentType;
import com.automo.state.entity.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends AbstractModel {

    @Column(nullable = false)
    @NotBlank(message = "Document is required")
    private String document;
    
    @Column(nullable = false)
    @NotBlank(message = "Identifier is required")
    private String identifier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private State state;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_type_id", nullable = false)
    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;
} 