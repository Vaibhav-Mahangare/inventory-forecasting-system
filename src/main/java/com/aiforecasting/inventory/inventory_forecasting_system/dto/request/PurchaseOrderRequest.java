package com.aiforecasting.inventory.inventory_forecasting_system.dto.request;


import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRequest {

    private Integer quantity;
    private LocalDate orderDate;
    private LocalDate expectedDelivery;
    private OrderStatus status;
    private Long productId;
    private Long supplierId;

}
