package com.aiforecasting.inventory.inventory_forecasting_system.entity;

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
    @OneToMany(mappedBy = "product")
    private List<Inventory> inventories;

    @OneToMany(mappedBy = "product")
    private List<Sale> sales;

    @OneToMany(mappedBy = "product")
    private List<Forecast> forecasts;

    @OneToMany(mappedBy = "product")
    private List<PurchaseOrder> purchaseOrders;

}
