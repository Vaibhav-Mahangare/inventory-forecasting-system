package com.aiforecasting.inventory.inventory_forecasting_system.dto.ml;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLForecastResult {

    @JsonAlias({"product_id", "productId"})
    private Long productId;

    @JsonAlias({"predicted_7_days", "predicted7Days"})
    private Integer predicted7Days;

    @JsonAlias({"predicted_15_days", "predicted15Days"})
    private Integer predicted15Days;

    @JsonAlias({"predicted_30_days", "predicted30Days"})
    private Integer predicted30Days;

    private Double mae;
    private Double rmse;
    private Double mape;

    @JsonAlias({"accuracy_label", "accuracyLabel"})
    private String accuracyLabel;

    @JsonAlias({"model_status", "modelStatus"})
    private String modelStatus;
}