package com.aiforecasting.inventory.inventory_forecasting_system.config;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Role;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.User;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default ADMIN on first startup if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@inventory.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Default ADMIN user created — username: admin, password: admin123");
        }

        // Create default INVENTORY_MANAGER on first startup if not exists
        if (!userRepository.existsByUsername("manager")) {
            User manager = User.builder()
                    .username("manager")
                    .email("manager@inventory.com")
                    .password(passwordEncoder.encode("manager123"))
                    .role(Role.INVENTORY_MANAGER)
                    .enabled(true)
                    .build();
            userRepository.save(manager);
            log.info("Default INVENTORY_MANAGER created — username: manager, password: manager123");
        }
    }
}
