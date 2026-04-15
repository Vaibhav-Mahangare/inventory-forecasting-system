package com.aiforecasting.inventory.inventory_forecasting_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forecasts")
public class Forecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long forecastId;

    private Integer predictedQuantity;
    private LocalDate forecastDate;
    private String modelVersion;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}