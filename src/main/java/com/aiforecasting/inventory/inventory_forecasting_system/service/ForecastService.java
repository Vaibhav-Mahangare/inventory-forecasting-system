package com.aiforecasting.inventory.inventory_forecasting_system.service;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.ForecastRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.ForecastResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;

import java.util.List;

public interface ForecastService {

    ForecastResponse createForecast(ForecastRequest request);
    ForecastResponse getLatestForecastByProduct(Long productId);
    List<ForecastResponse> getAllForecastsByProduct(Long productId);
    void deleteForecast(Long id);

    // Called internally by scheduler or inventory service
    void triggerForecastBasedReorder(Product product);
}