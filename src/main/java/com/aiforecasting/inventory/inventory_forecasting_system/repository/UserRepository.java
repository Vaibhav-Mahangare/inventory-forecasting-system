package com.aiforecasting.inventory.inventory_forecasting_system.repository;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.User;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}
