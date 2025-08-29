package com.automo.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/test")
@Tag(name = "Test", description = "Test endpoints to verify SpringDoc functionality")
public class TestController {

    @GetMapping("/hello")
    @Operation(description = "Simple hello endpoint", summary = "Hello World")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World! SpringDoc should be working.");
    }

    @GetMapping("/ping")
    @Operation(description = "Ping endpoint", summary = "Ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong! Endpoint accessible.");
    }

    @GetMapping("/info")
    @Operation(description = "Info endpoint", summary = "Info")
    public ResponseEntity<String> info() {
        return ResponseEntity.ok("Test controller is working. SpringDoc should detect this.");
    }
}
