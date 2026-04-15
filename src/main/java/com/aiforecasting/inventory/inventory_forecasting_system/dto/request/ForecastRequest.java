package com.aiforecasting.inventory.inventory_forecasting_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForecastRequest {

    private Integer predictedQuantity;
    private LocalDate forecastDate;
    private String modelVersion;
    private Long productId;

}
