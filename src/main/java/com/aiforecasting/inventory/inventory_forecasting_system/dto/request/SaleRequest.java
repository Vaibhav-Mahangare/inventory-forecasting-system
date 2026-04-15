package com.aiforecasting.inventory.inventory_forecasting_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleRequest {

    private Integer quantitySold;
    private LocalDate saleDate;
    private Long productId;
    private Long warehouseId;

}