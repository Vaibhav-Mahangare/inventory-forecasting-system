package com.aiforecasting.inventory.inventory_forecasting_system.controller;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.AlertRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.AlertResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@RequestBody AlertRequest request) {
        return new ResponseEntity<>(alertService.createAlert(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<AlertResponse>> getAlertsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(alertService.getAlertsByProduct(productId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<AlertResponse>> getAlertsByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(alertService.getAlertsByWarehouse(warehouseId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.ok("Alert deleted successfully");
    }
}