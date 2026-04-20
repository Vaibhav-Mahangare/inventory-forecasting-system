package com.aiforecasting.inventory.inventory_forecasting_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warehouseId;

    private String name;
    private String location;

    @JsonIgnore
    @OneToMany(mappedBy = "warehouse")
    private List<Inventory> inventories;

    @JsonIgnore
    @OneToMany(mappedBy = "warehouse")
    private List<Sale> sales;
}