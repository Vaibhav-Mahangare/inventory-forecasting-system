package com.aiforecasting.inventory.inventory_forecasting_system.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLBatchForecastResponse {

    @JsonProperty("results")
    private List<MLForecastResult> results;

    @JsonProperty("total_products")
    private Integer totalProducts;

    @JsonProperty("successful")
    private Integer successful;

    @JsonProperty("failed")
    private Integer failed;
}
