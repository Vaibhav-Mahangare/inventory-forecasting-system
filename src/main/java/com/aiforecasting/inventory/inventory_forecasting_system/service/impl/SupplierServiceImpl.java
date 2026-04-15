package com.aiforecasting.inventory.inventory_forecasting_system.service.impl;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.SupplierRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.SupplierResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Supplier;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.SupplierRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setContactEmail(request.getContactEmail());
        supplier.setPhone(request.getPhone());

        Supplier saved = supplierRepository.save(supplier);
        return mapToResponse(saved);
    }

    @Override
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return mapToResponse(supplier);
    }

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        supplier.setName(request.getName());
        supplier.setContactEmail(request.getContactEmail());
        supplier.setPhone(request.getPhone());

        Supplier updated = supplierRepository.save(supplier);
        return mapToResponse(updated);
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        supplierRepository.delete(supplier);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        SupplierResponse response = new SupplierResponse();
        response.setSupplierId(supplier.getSupplierId());
        response.setName(supplier.getName());
        response.setContactEmail(supplier.getContactEmail());
        response.setPhone(supplier.getPhone());
        return response;
    }
}
