package com.aiforecasting.inventory.inventory_forecasting_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseRequest {

    private String name;
    private String location;

}