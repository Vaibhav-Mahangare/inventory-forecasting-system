package com.aiforecasting.inventory.inventory_forecasting_system.repository;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByContactEmail(String contactEmail);

    Optional<Supplier> findByName(String name);
}