package com.aiforecasting.inventory.inventory_forecasting_system.service.impl;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.SaleRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.SaleResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.*;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.*;
import com.aiforecasting.inventory.inventory_forecasting_system.service.InventoryService;
import com.aiforecasting.inventory.inventory_forecasting_system.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public SaleResponse createSale(SaleRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + request.getWarehouseId()));

        // Deduct stock from inventory
        Inventory inventory = inventoryRepository.findByProductAndWarehouse(product, warehouse)
                .orElseThrow(() -> new RuntimeException("Inventory not found for given product and warehouse"));

        if (inventory.getQuantity() < request.getQuantitySold()) {
            throw new RuntimeException("Insufficient stock. Available: " + inventory.getQuantity());
        }

        // Deduct and save — this will also trigger low stock check via InventoryService
        inventory.setQuantity(inventory.getQuantity() - request.getQuantitySold());
        inventoryRepository.save(inventory);

        Sale sale = new Sale();
        sale.setQuantitySold(request.getQuantitySold());
        sale.setSaleDate(request.getSaleDate());
        sale.setProduct(product);
        sale.setWarehouse(warehouse);

        Sale saved = saleRepository.save(sale);
        return mapToResponse(saved);
    }

    @Override
    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));
        return mapToResponse(sale);
    }

    @Override
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleResponse> getSalesByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return saleRepository.findByProduct(product)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SaleResponse> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));
        saleRepository.delete(sale);
    }

    private SaleResponse mapToResponse(Sale sale) {
        SaleResponse response = new SaleResponse();
        response.setSaleId(sale.getSaleId());
        response.setQuantitySold(sale.getQuantitySold());
        response.setSaleDate(sale.getSaleDate());
        response.setProductId(sale.getProduct().getProductId());
        response.setProductName(sale.getProduct().getName());
        response.setWarehouseId(sale.getWarehouse().getWarehouseId());
        response.setWarehouseName(sale.getWarehouse().getName());
        return response;
    }
}