package com.aiforecasting.inventory.inventory_forecasting_system.controller;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.ForecastRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.ForecastResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.service.ForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecasts")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @PostMapping
    public ResponseEntity<ForecastResponse> createForecast(@RequestBody ForecastRequest request) {
        return new ResponseEntity<>(forecastService.createForecast(request), HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}/latest")
    public ResponseEntity<ForecastResponse> getLatestForecastByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(forecastService.getLatestForecastByProduct(productId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ForecastResponse>> getAllForecastsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(forecastService.getAllForecastsByProduct(productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteForecast(@PathVariable Long id) {
        forecastService.deleteForecast(id);
        return ResponseEntity.ok("Forecast deleted successfully");
    }
}