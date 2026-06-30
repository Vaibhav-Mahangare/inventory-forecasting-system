package com.aiforecasting.inventory.inventory_forecasting_system.config;

import com.aiforecasting.inventory.inventory_forecasting_system.filter.JwtAuthFilter;
import com.aiforecasting.inventory.inventory_forecasting_system.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── Public endpoints ──────────────────────────────────
                        .requestMatchers("/api/auth/**").permitAll()

                        // ── ADMIN only endpoints ──────────────────────────────
                        // User management
                        .requestMatchers("/api/auth/users/**").hasRole("ADMIN")
                        // Delete products, warehouses, suppliers — ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/warehouses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/suppliers/**").hasRole("ADMIN")

                        // ── ADMIN + INVENTORY_MANAGER endpoints ───────────────
                        // Products — both can read and create/update
                        .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        // Warehouses
                        .requestMatchers(HttpMethod.GET, "/api/warehouses/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/warehouses/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/warehouses/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        // Inventory — full access both roles
                        .requestMatchers("/api/inventories/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        // Sales — full access both roles
                        .requestMatchers("/api/sales/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        // Suppliers
                        .requestMatchers(HttpMethod.GET, "/api/suppliers/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/suppliers/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/suppliers/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        // Purchase Orders — full access both roles
                        .requestMatchers("/api/purchase-orders/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        // Forecasts — both can view, only ADMIN can delete
                        .requestMatchers(HttpMethod.GET, "/api/forecasts/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/forecasts/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/forecasts/**").hasRole("ADMIN")
                        // ML Forecast — both roles
                        .requestMatchers("/api/ml/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        // Alerts — both can view, only ADMIN can delete
                        .requestMatchers(HttpMethod.GET, "/api/alerts/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/alerts/**").hasAnyRole("ADMIN","INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/alerts/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
