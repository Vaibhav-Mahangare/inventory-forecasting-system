package com.aiforecasting.inventory.inventory_forecasting_system.service;

import com.aiforecasting.inventory.inventory_forecasting_system.config.JwtUtil;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.auth.AuthResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.auth.LoginRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.auth.RegisterRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.auth.UserResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Role;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.User;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    // -------------------------------------------------------
    // Register new user
    // -------------------------------------------------------
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.INVENTORY_MANAGER)
                .enabled(true)
                .build();

        userRepository.save(user);
        log.info("New user registered: {} with role: {}", user.getUsername(), user.getRole());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Registration successful")
                .build();
    }

    // -------------------------------------------------------
    // Login existing user
    // -------------------------------------------------------
    public AuthResponse login(LoginRequest request) {
        // This throws BadCredentialsException if invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        log.info("User logged in: {} with role: {}", user.getUsername(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    // -------------------------------------------------------
    // Get all users — ADMIN only
    // -------------------------------------------------------
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Toggle user enabled/disabled — ADMIN only
    // -------------------------------------------------------
    public UserResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return mapToUserResponse(user);
    }

    // -------------------------------------------------------
    // Delete user — ADMIN only
    // -------------------------------------------------------
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
