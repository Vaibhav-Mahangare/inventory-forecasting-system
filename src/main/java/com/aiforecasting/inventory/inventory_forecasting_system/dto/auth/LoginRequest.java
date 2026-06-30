package com.aiforecasting.inventory.inventory_forecasting_system.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
