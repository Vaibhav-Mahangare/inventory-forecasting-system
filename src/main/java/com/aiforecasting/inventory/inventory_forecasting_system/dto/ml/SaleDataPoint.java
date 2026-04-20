package com.aiforecasting.inventory.inventory_forecasting_system.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleDataPoint {

    @JsonProperty("sale_date")
    private String saleDate;        // format: "YYYY-MM-DD"

    @JsonProperty("quantity_sold")
    private Integer quantitySold;
}
