package com.aiforecasting.inventory.inventory_forecasting_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleResponse {

    private Long saleId;
    private Integer quantitySold;
    private LocalDate saleDate;
    private Long productId;
    private String productName;     // flattened from Product
    private Long warehouseId;
    private String warehouseName;   // flattened from Warehouse

}
