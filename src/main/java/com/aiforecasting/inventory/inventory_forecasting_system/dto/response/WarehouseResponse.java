package com.aiforecasting.inventory.inventory_forecasting_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResponse {

    private Long warehouseId;
    private String name;
    private String location;

}