package com.aiforecasting.inventory.inventory_forecasting_system.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLBatchForecastRequest {

    @JsonProperty("products")
    private List<MLForecastRequest> products;
}