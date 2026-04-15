package com.aiforecasting.inventory.inventory_forecasting_system.service.impl;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.ForecastRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.ForecastResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Forecast;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.ForecastRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.InventoryRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.ProductRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.service.ForecastService;
import com.aiforecasting.inventory.inventory_forecasting_system.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForecastServiceImpl implements ForecastService {

    private final ForecastRepository forecastRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PurchaseOrderService purchaseOrderService;

    @Override
    public ForecastResponse createForecast(ForecastRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        Forecast forecast = new Forecast();
        forecast.setPredictedQuantity(request.getPredictedQuantity());
        forecast.setForecastDate(request.getForecastDate());
        forecast.setModelVersion(request.getModelVersion());
        forecast.setProduct(product);

        Forecast saved = forecastRepository.save(forecast);

        // After saving a forecast, check if reorder is needed
        triggerForecastBasedReorder(product);

        return mapToResponse(saved);
    }

    @Override
    public ForecastResponse getLatestForecastByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Forecast forecast = forecastRepository.findTopByProductOrderByForecastDateDesc(product)
                .orElseThrow(() -> new RuntimeException("No forecast found for product: " + productId));

        return mapToResponse(forecast);
    }

    @Override
    public List<ForecastResponse> getAllForecastsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        return forecastRepository.findByProduct(product)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteForecast(Long id) {
        Forecast forecast = forecastRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forecast not found with id: " + id));
        forecastRepository.delete(forecast);
    }


    // Core Business Logic — Forecast Based Reorder
    // If forecast predicts demand > current stock, auto order
    @Override
    public void triggerForecastBasedReorder(Product product) {
        forecastRepository.findTopByProductOrderByForecastDateDesc(product)
                .ifPresent(forecast -> {
                    int totalStock = inventoryRepository.findByProduct(product)
                            .stream()
                            .mapToInt(i -> i.getQuantity())
                            .sum();

                    // If predicted demand exceeds current total stock — auto reorder
                    if (forecast.getPredictedQuantity() > totalStock) {
                        purchaseOrderService.autoCreatePurchaseOrder(product);
                    }
                });
    }

    private ForecastResponse mapToResponse(Forecast forecast) {
        ForecastResponse response = new ForecastResponse();
        response.setForecastId(forecast.getForecastId());
        response.setPredictedQuantity(forecast.getPredictedQuantity());
        response.setForecastDate(forecast.getForecastDate());
        response.setModelVersion(forecast.getModelVersion());
        response.setProductId(forecast.getProduct().getProductId());
        response.setProductName(forecast.getProduct().getName());
        return response;
    }
}