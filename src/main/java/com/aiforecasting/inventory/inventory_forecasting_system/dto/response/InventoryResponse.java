package com.aiforecasting.inventory.inventory_forecasting_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {

    private Long inventoryId;
    private Integer quantity;
    private Integer reorderPoint;
    private LocalDateTime lastUpdated;
    private Long productId;
    private String productName;     // flattened from Product
    private Long warehouseId;
    private String warehouseName;   // flattened from Warehouse

}
