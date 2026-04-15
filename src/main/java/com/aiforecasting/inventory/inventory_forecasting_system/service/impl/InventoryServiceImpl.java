package com.aiforecasting.inventory.inventory_forecasting_system.service.impl;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.InventoryRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.InventoryResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Inventory;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Warehouse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.AlertType;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.OrderStatus;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.InventoryRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.ProductRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.WarehouseRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.service.AlertService;
import com.aiforecasting.inventory.inventory_forecasting_system.service.InventoryService;
import com.aiforecasting.inventory.inventory_forecasting_system.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final AlertService alertService;
    private final PurchaseOrderService purchaseOrderService;

    @Override
    public InventoryResponse createInventory(InventoryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + request.getWarehouseId()));

        Inventory inventory = new Inventory();
        inventory.setQuantity(request.getQuantity());
        inventory.setReorderPoint(request.getReorderPoint());
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setLastUpdated(LocalDateTime.now());

        Inventory saved = inventoryRepository.save(inventory);

        // Auto-check low stock after creation
        checkAndHandleLowStock(saved);

        return mapToResponse(saved);
    }

    @Override
    public InventoryResponse getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        return mapToResponse(inventory);
    }

    @Override
    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getInventoryByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return inventoryRepository.findByProduct(product)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryResponse> getInventoryByWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + warehouseId));
        return inventoryRepository.findByWarehouse(warehouse)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + request.getWarehouseId()));

        inventory.setQuantity(request.getQuantity());
        inventory.setReorderPoint(request.getReorderPoint());
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setLastUpdated(LocalDateTime.now());

        Inventory updated = inventoryRepository.save(inventory);

        // Auto-check low stock after every update
        checkAndHandleLowStock(updated);

        return mapToResponse(updated);
    }

    @Override
    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        inventoryRepository.delete(inventory);
    }

    @Override
    public List<InventoryResponse> getLowStockInventories() {
        return inventoryRepository.findLowStockInventories()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    // Core Business Logic — Low Stock Check
    // Runs every time inventory is created or updated
    private void checkAndHandleLowStock(Inventory inventory) {
        if (inventory.getQuantity() <= inventory.getReorderPoint()) {

            // 1. Create a LOW_STOCK alert
            alertService.createLowStockAlert(
                    inventory.getProduct(),
                    inventory.getWarehouse()
            );

            // 2. Auto-create a PurchaseOrder if one doesn't already exist
            purchaseOrderService.autoCreatePurchaseOrder(
                    inventory.getProduct()
            );
        }
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.setInventoryId(inventory.getInventoryId());
        response.setQuantity(inventory.getQuantity());
        response.setReorderPoint(inventory.getReorderPoint());
        response.setLastUpdated(inventory.getLastUpdated());
        response.setProductId(inventory.getProduct().getProductId());
        response.setProductName(inventory.getProduct().getName());
        response.setWarehouseId(inventory.getWarehouse().getWarehouseId());
        response.setWarehouseName(inventory.getWarehouse().getName());
        return response;
    }
}