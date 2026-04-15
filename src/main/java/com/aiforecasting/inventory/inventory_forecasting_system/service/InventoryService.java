package com.aiforecasting.inventory.inventory_forecasting_system.service;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.InventoryRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse createInventory(InventoryRequest request);
    InventoryResponse getInventoryById(Long id);
    List<InventoryResponse> getAllInventories();
    List<InventoryResponse> getInventoryByProduct(Long productId);
    List<InventoryResponse> getInventoryByWarehouse(Long warehouseId);
    InventoryResponse updateInventory(Long id, InventoryRequest request);
    void deleteInventory(Long id);
    List<InventoryResponse> getLowStockInventories();
}