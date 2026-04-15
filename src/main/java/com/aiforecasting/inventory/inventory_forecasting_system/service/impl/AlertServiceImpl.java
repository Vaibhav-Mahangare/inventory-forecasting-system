package com.aiforecasting.inventory.inventory_forecasting_system.service.impl;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.AlertRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.AlertResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Alert;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Warehouse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.AlertType;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.AlertRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.ProductRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.WarehouseRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public AlertResponse createAlert(AlertRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + request.getWarehouseId()));

        Alert alert = new Alert();
        alert.setAlertType(request.getAlertType());
        alert.setMessage(request.getMessage());
        alert.setProduct(product);
        alert.setWarehouse(warehouse);
        alert.setCreatedAt(LocalDateTime.now());

        Alert saved = alertRepository.save(alert);
        return mapToResponse(saved);
    }

    @Override
    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponse> getAlertsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return alertRepository.findByProduct(product)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertResponse> getAlertsByWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + warehouseId));
        return alertRepository.findByWarehouse(warehouse)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAlert(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found with id: " + id));
        alertRepository.delete(alert);
    }

    // -------------------------------------------------------
    // Core Business Logic — Auto Low Stock Alert
    // Called by InventoryService when quantity <= reorderPoint
    // -------------------------------------------------------
    @Override
    public void createLowStockAlert(Product product, Warehouse warehouse) {
        Alert alert = new Alert();
        alert.setAlertType(AlertType.LOW_STOCK);
        alert.setMessage("Low stock alert for product: "
                + product.getName()
                + " at warehouse: "
                + warehouse.getName());
        alert.setProduct(product);
        alert.setWarehouse(warehouse);
        alert.setCreatedAt(LocalDateTime.now());
        alertRepository.save(alert);
    }

    private AlertResponse mapToResponse(Alert alert) {
        AlertResponse response = new AlertResponse();
        response.setAlertId(alert.getAlertId());
        response.setAlertType(alert.getAlertType());
        response.setMessage(alert.getMessage());
        response.setCreatedAt(alert.getCreatedAt());
        response.setProductId(alert.getProduct().getProductId());
        response.setProductName(alert.getProduct().getName());
        response.setWarehouseId(alert.getWarehouse().getWarehouseId());
        response.setWarehouseName(alert.getWarehouse().getName());
        return response;
    }
}
