package com.automo.accountType.controller;

import com.automo.accountType.dto.AccountTypeDto;
import com.automo.accountType.response.AccountTypeResponse;
import com.automo.accountType.service.AccountTypeService;
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
@RequestMapping("/account-types")
@RequiredArgsConstructor
@Tag(name = "Account Types", description = "Account type management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AccountTypeController {

    private final AccountTypeService accountTypeService;

    @Operation(description = "List all account types", summary = "Get all account types")
    @ApiResponse(responseCode = "200", description = "Account types retrieved successfully")
    @GetMapping
    public ResponseEntity<List<AccountTypeResponse>> getAllAccountTypes() {
        return ResponseEntity.ok(accountTypeService.getAllAccountTypes());
    }

    @Operation(description = "Get account type by ID", summary = "Get a specific account type by ID")
    @ApiResponse(responseCode = "200", description = "Account type retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<AccountTypeResponse> getAccountTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(accountTypeService.getAccountTypeByIdResponse(id));
    }

    @Operation(description = "Create new account type", summary = "Create a new account type")
    @ApiResponse(responseCode = "201", description = "Account type created successfully")
    @PostMapping
    public ResponseEntity<AccountTypeResponse> createAccountType(@Valid @RequestBody AccountTypeDto accountTypeDto) {
        AccountTypeResponse response = accountTypeService.createAccountType(accountTypeDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update account type", summary = "Update an existing account type")
    @ApiResponse(responseCode = "200", description = "Account type updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<AccountTypeResponse> updateAccountType(@PathVariable Long id, @Valid @RequestBody AccountTypeDto accountTypeDto) {
        return ResponseEntity.ok(accountTypeService.updateAccountType(id, accountTypeDto));
    }

    @Operation(description = "Delete account type", summary = "Delete an account type")
    @ApiResponse(responseCode = "204", description = "Account type deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountType(@PathVariable Long id) {
        accountTypeService.deleteAccountType(id);
        return ResponseEntity.noContent().build();
    }
} 