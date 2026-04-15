package com.aiforecasting.inventory.inventory_forecasting_system.service.impl;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.WarehouseRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.WarehouseResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Warehouse;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.WarehouseRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());

        Warehouse saved = warehouseRepository.save(warehouse);
        return mapToResponse(saved);
    }

    @Override
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        return mapToResponse(warehouse);
    }

    @Override
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));

        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());

        Warehouse updated = warehouseRepository.save(warehouse);
        return mapToResponse(updated);
    }

    @Override
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        warehouseRepository.delete(warehouse);
    }

    private WarehouseResponse mapToResponse(Warehouse warehouse) {
        WarehouseResponse response = new WarehouseResponse();
        response.setWarehouseId(warehouse.getWarehouseId());
        response.setName(warehouse.getName());
        response.setLocation(warehouse.getLocation());
        return response;
    }
}
