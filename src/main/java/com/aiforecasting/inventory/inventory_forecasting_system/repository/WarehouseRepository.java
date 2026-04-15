package com.aiforecasting.inventory.inventory_forecasting_system.repository;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByName(String name);

    List<Warehouse> findByLocation(String location);
}