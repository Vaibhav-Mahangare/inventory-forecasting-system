package com.aiforecasting.inventory.inventory_forecasting_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForecastResponse {

    private Long forecastId;
    private Integer predictedQuantity;
    private LocalDate forecastDate;
    private String modelVersion;
    private Long productId;
    private String productName;     // flattened from Product

}