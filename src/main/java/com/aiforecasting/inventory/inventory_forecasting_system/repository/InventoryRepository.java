package com.aiforecasting.inventory.inventory_forecasting_system.repository;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Inventory;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProduct(Product product);

    List<Inventory> findByWarehouse(Warehouse warehouse);

    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);

    // Fetch all inventory where stock has fallen at or below reorder point
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.reorderPoint")
    List<Inventory> findLowStockInventories();
}
