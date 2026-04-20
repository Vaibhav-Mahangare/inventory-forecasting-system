package com.aiforecasting.inventory.inventory_forecasting_system.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLForecastRequest {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("sales_data")
    private List<SaleDataPoint> salesData;

    @JsonProperty("force_retrain")
    private Boolean forceRetrain = false;
}