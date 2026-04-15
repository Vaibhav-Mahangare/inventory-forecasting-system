package com.aiforecasting.inventory.inventory_forecasting_system.dto.response;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.AlertType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertResponse {

    private Long alertId;
    private AlertType alertType;
    private String message;
    private LocalDateTime createdAt;
    private Long productId;
    private String productName;     // flattened from Product
    private Long warehouseId;
    private String warehouseName;   // flattened from Warehouse

}