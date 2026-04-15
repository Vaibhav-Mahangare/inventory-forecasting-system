package com.aiforecasting.inventory.inventory_forecasting_system.service;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.AlertRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.AlertResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Warehouse;

import java.util.List;

public interface AlertService {

    AlertResponse createAlert(AlertRequest request);
    List<AlertResponse> getAllAlerts();
    List<AlertResponse> getAlertsByProduct(Long productId);
    List<AlertResponse> getAlertsByWarehouse(Long warehouseId);
    void deleteAlert(Long id);

    // Called internally by InventoryService when stock is low
    void createLowStockAlert(Product product, Warehouse warehouse);
}
