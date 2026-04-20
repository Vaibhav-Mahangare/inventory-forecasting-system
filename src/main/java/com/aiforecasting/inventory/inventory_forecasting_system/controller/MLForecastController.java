package com.aiforecasting.inventory.inventory_forecasting_system.controller;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.ml.MLForecastResult;
import com.aiforecasting.inventory.inventory_forecasting_system.service.MLForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ml")
@RequiredArgsConstructor
public class MLForecastController {

    private final MLForecastService mlForecastService;

    // Single product forecast
    @GetMapping("/forecast/{productId}")
    public ResponseEntity<MLForecastResult> getForecast(
            @PathVariable Long productId) {
        return ResponseEntity.ok(
                mlForecastService.getForecastForProduct(productId));
    }

    // Single product forecast with force retrain
    @GetMapping("/forecast/{productId}/retrain")
    public ResponseEntity<MLForecastResult> getForecastWithRetrain(
            @PathVariable Long productId) {
        return ResponseEntity.ok(
                mlForecastService.getForecastForProduct(productId, true));
    }

    // Batch forecast for all products
    @GetMapping("/forecast/batch/all")
    public ResponseEntity<List<MLForecastResult>> getAllProductForecasts() {
        return ResponseEntity.ok(
                mlForecastService.getForecastForAllProducts());
    }
}
