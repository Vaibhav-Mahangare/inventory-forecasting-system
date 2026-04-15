package com.aiforecasting.inventory.inventory_forecasting_system.repository;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Alert;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByProduct(Product product);

    List<Alert> findByAlertType(String alertType);

    List<Alert> findByWarehouse(Warehouse warehouse);

    List<Alert> findByProductAndAlertType(Product product, String alertType);

    // Find all unresolved alerts for a specific warehouse
    List<Alert> findByWarehouseAndAlertType(Warehouse warehouse, String alertType);
}
