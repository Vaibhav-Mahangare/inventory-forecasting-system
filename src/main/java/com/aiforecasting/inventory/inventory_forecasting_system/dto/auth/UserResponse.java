package com.aiforecasting.inventory.inventory_forecasting_system.dto.auth;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
}

