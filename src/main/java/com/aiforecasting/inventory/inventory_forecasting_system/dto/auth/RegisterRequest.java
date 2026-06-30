package com.aiforecasting.inventory.inventory_forecasting_system.dto.auth;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role;  // ADMIN or INVENTORY_MANAGER
}
