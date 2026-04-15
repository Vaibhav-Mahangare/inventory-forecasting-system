package com.aiforecasting.inventory.inventory_forecasting_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long productId;
    private String name;
    private String category;
    private Double price;
    private Integer leadTimeDays;
    private LocalDateTime createdAt;

}