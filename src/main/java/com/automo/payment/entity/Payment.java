package com.automo.payment.entity;

import com.automo.identifier.entity.Identifier;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifier_id", nullable = false)
    @NotNull(message = "Identifier is required")
    private Identifier identifier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    @NotNull(message = "State is required")
    private State state;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_type_id", nullable = false)
    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;
    
    @Column(name = "image_filename")
    private String imageFilename;
    
    @Column(name = "original_filename")
    private String originalFilename;
    
    @Column(name = "amount", precision = 10, scale = 2)
    private java.math.BigDecimal amount;
} 