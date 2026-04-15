package com.aiforecasting.inventory.inventory_forecasting_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequest {

    private String name;
    private String contactEmail;
    private String phone;

}