package com.automo.agentProduct.controller;

import com.automo.agentProduct.dto.AgentProductDto;
import com.automo.agentProduct.response.AgentProductResponse;
import com.automo.agentProduct.service.AgentProductService;
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
@RequestMapping("/agent-products")
@RequiredArgsConstructor
@Tag(name = "Agent Products", description = "Agent Product management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AgentProductController {

    private final AgentProductService agentProductService;

    @Operation(description = "List all agent products", summary = "Get all agent products")
    @ApiResponse(responseCode = "200", description = "Agent products retrieved successfully")
    @GetMapping
    public ResponseEntity<List<AgentProductResponse>> getAllAgentProducts() {
        return ResponseEntity.ok(agentProductService.getAllAgentProducts());
    }

    @Operation(description = "Get agent product by ID", summary = "Get a specific agent product by ID")
    @ApiResponse(responseCode = "200", description = "Agent product retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<AgentProductResponse> getAgentProductById(@PathVariable Long id) {
        return ResponseEntity.ok(agentProductService.getAgentProductByIdResponse(id));
    }

    @Operation(description = "Create new agent product", summary = "Create a new agent product")
    @ApiResponse(responseCode = "201", description = "Agent product created successfully")
    @PostMapping
    public ResponseEntity<AgentProductResponse> createAgentProduct(@Valid @RequestBody AgentProductDto agentProductDto) {
        AgentProductResponse response = agentProductService.createAgentProduct(agentProductDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update agent product", summary = "Update an existing agent product")
    @ApiResponse(responseCode = "200", description = "Agent product updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<AgentProductResponse> updateAgentProduct(@PathVariable Long id, @Valid @RequestBody AgentProductDto agentProductDto) {
        return ResponseEntity.ok(agentProductService.updateAgentProduct(id, agentProductDto));
    }

    @Operation(description = "Delete agent product", summary = "Delete an agent product")
    @ApiResponse(responseCode = "204", description = "Agent product deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgentProduct(@PathVariable Long id) {
        agentProductService.deleteAgentProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get agent products by agent", summary = "Get agent products filtered by agent ID")
    @ApiResponse(responseCode = "200", description = "Agent products retrieved successfully")
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<AgentProductResponse>> getAgentProductsByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(agentProductService.getAgentProductsByAgent(agentId));
    }

    @Operation(description = "Get agent products by product", summary = "Get agent products filtered by product ID")
    @ApiResponse(responseCode = "200", description = "Agent products retrieved successfully")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<AgentProductResponse>> getAgentProductsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(agentProductService.getAgentProductsByProduct(productId));
    }

    @Operation(description = "Get agent product by agent and product", summary = "Get agent product by agent ID and product ID")
    @ApiResponse(responseCode = "200", description = "Agent product retrieved successfully")
    @GetMapping("/agent/{agentId}/product/{productId}")
    public ResponseEntity<AgentProductResponse> getAgentProductByAgentAndProduct(@PathVariable Long agentId, @PathVariable Long productId) {
        return ResponseEntity.ok(agentProductService.getAgentProductByAgentAndProduct(agentId, productId));
    }
} 