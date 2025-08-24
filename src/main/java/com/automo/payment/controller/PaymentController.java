package com.automo.payment.controller;

import com.automo.payment.dto.PaymentDto;
import com.automo.payment.response.PaymentResponse;
import com.automo.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(description = "List all payments", summary = "Get all payments")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @Operation(description = "Get payment by ID", summary = "Get a specific payment by ID")
    @ApiResponse(responseCode = "200", description = "Payment retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentByIdResponse(id));
    }

    @Operation(description = "Create new payment", summary = "Create a new payment")
    @ApiResponse(responseCode = "201", description = "Payment created successfully")
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentDto paymentDto) {
        PaymentResponse response = paymentService.createPayment(paymentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update payment", summary = "Update an existing payment")
    @ApiResponse(responseCode = "200", description = "Payment updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentDto paymentDto) {
        return ResponseEntity.ok(paymentService.updatePayment(id, paymentDto));
    }

    @Operation(description = "Delete payment", summary = "Delete a payment")
    @ApiResponse(responseCode = "204", description = "Payment deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get payments by state", summary = "Get payments filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(paymentService.getPaymentsByState(stateId));
    }

    @Operation(description = "Get payments by type", summary = "Get payments filtered by payment type ID")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    @GetMapping("/type/{paymentTypeId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByType(@PathVariable Long paymentTypeId) {
        return ResponseEntity.ok(paymentService.getPaymentsByType(paymentTypeId));
    }

    @Operation(description = "Get payments by identifier", summary = "Get payments filtered by identifier")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    @GetMapping("/identifier/{identifier}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByIdentifier(@PathVariable String identifier) {
        return ResponseEntity.ok(paymentService.getPaymentsByIdentifier(identifier));
    }
} 