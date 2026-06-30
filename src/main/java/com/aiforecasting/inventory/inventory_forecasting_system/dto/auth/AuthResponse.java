package com.aiforecasting.inventory.inventory_forecasting_system.dto.auth;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private Role role;
    private String message;
}
