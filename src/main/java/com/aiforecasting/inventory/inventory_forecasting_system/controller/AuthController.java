package com.aiforecasting.inventory.inventory_forecasting_system.controller;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.auth.AuthResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.auth.LoginRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.auth.RegisterRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.auth.UserResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── Public ────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ── ADMIN only ────────────────────────────────────────────
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PatchMapping("/users/{userId}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.toggleUserStatus(userId));
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        authService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}
