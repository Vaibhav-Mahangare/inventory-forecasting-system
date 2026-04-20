package com.aiforecasting.inventory.inventory_forecasting_system.dto.ml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLForecastResult {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("predicted_7_days")
    private Integer predicted7Days;

    @JsonProperty("predicted_15_days")
    private Integer predicted15Days;

    @JsonProperty("predicted_30_days")
    private Integer predicted30Days;

    @JsonProperty("mae")
    private Double mae;

    @JsonProperty("rmse")
    private Double rmse;

    @JsonProperty("mape")
    private Double mape;

    @JsonProperty("accuracy_label")
    private String accuracyLabel;

    @JsonProperty("model_status")
    private String modelStatus;
}