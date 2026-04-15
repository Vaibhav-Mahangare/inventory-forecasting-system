package com.aiforecasting.inventory.inventory_forecasting_system.service;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.SaleRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.SaleResponse;

import java.time.LocalDate;
import java.util.List;

public interface SaleService {

    SaleResponse createSale(SaleRequest request);
    SaleResponse getSaleById(Long id);
    List<SaleResponse> getAllSales();
    List<SaleResponse> getSalesByProduct(Long productId);
    List<SaleResponse> getSalesByDateRange(LocalDate startDate, LocalDate endDate);
    void deleteSale(Long id);
}