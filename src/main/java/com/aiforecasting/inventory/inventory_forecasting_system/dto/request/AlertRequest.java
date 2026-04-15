package com.aiforecasting.inventory.inventory_forecasting_system.dto.request;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.AlertType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {

    private AlertType alertType;
    private String message;
    private Long productId;
    private Long warehouseId;

}
