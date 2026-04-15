package com.aiforecasting.inventory.inventory_forecasting_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // it includes @setter @getter @tostring @requiredargsconstructor, etc.
@NoArgsConstructor
@AllArgsConstructor

public class ProductRequest {

    private String name;
    private String category;
    private Double price;
    private Integer leadTimeDays;

}