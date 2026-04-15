package com.aiforecasting.inventory.inventory_forecasting_system.repository;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.PurchaseOrder;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Supplier;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findBySupplier(Supplier supplier);

    List<PurchaseOrder> findByProduct(Product product);

    List<PurchaseOrder> findByStatus(OrderStatus status);

    List<PurchaseOrder> findByExpectedDeliveryBefore(LocalDate date);

    // Useful for checking if a pending order already exists for a product
    boolean existsByProductAndStatus(Product product, OrderStatus status);
}