package com.automo.paymentType.controller;

import com.automo.paymentType.dto.PaymentTypeDto;
import com.automo.paymentType.response.PaymentTypeResponse;
import com.automo.paymentType.service.PaymentTypeService;
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
@RequestMapping("/payment-types")
@RequiredArgsConstructor
@Tag(name = "Payment Types", description = "Payment type management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PaymentTypeController {

    private final PaymentTypeService paymentTypeService;

    @Operation(description = "List all payment types", summary = "Get all payment types")
    @ApiResponse(responseCode = "200", description = "Payment types retrieved successfully")
    @GetMapping
    public ResponseEntity<List<PaymentTypeResponse>> getAllPaymentTypes() {
        return ResponseEntity.ok(paymentTypeService.getAllPaymentTypes());
    }

    @Operation(description = "Get payment type by ID", summary = "Get a specific payment type by ID")
    @ApiResponse(responseCode = "200", description = "Payment type retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentTypeResponse> getPaymentTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentTypeService.getPaymentTypeByIdResponse(id));
    }

    @Operation(description = "Create new payment type", summary = "Create a new payment type")
    @ApiResponse(responseCode = "201", description = "Payment type created successfully")
    @PostMapping
    public ResponseEntity<PaymentTypeResponse> createPaymentType(@Valid @RequestBody PaymentTypeDto paymentTypeDto) {
        PaymentTypeResponse response = paymentTypeService.createPaymentType(paymentTypeDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update payment type", summary = "Update an existing payment type")
    @ApiResponse(responseCode = "200", description = "Payment type updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentTypeResponse> updatePaymentType(@PathVariable Long id, @Valid @RequestBody PaymentTypeDto paymentTypeDto) {
        return ResponseEntity.ok(paymentTypeService.updatePaymentType(id, paymentTypeDto));
    }

    @Operation(description = "Delete payment type", summary = "Delete a payment type")
    @ApiResponse(responseCode = "204", description = "Payment type deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentType(@PathVariable Long id) {
        paymentTypeService.deletePaymentType(id);
        return ResponseEntity.noContent().build();
    }
} 