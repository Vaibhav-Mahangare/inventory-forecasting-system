package com.aiforecasting.inventory.inventory_forecasting_system.dto.response;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderResponse {

    private Long orderId;
    private Integer quantity;
    private LocalDate orderDate;
    private LocalDate expectedDelivery;
    private OrderStatus status;
    private Long productId;
    private String productName;     // flattened from Product
    private Long supplierId;
    private String supplierName;    // flattened from Supplier

}