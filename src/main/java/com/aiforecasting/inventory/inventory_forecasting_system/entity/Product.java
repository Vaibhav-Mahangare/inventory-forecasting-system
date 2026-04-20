package com.aiforecasting.inventory.inventory_forecasting_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private String category;
    private Double price;
    private Integer leadTimeDays;

    private LocalDateTime createdAt;

    // Relationships
    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<Inventory> inventories;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<Sale> sales;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<Forecast> forecasts;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<PurchaseOrder> purchaseOrders;

}
