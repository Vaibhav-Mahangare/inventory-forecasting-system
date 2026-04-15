package com.aiforecasting.inventory.inventory_forecasting_system.service;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.SupplierRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {

    SupplierResponse createSupplier(SupplierRequest request);
    SupplierResponse getSupplierById(Long id);
    List<SupplierResponse> getAllSuppliers();
    SupplierResponse updateSupplier(Long id, SupplierRequest request);
    void deleteSupplier(Long id);
}