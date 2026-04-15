package com.aiforecasting.inventory.inventory_forecasting_system.service;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.PurchaseOrderRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.PurchaseOrderResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.OrderStatus;

import java.util.List;

public interface PurchaseOrderService {

    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request);
    PurchaseOrderResponse getPurchaseOrderById(Long id);
    List<PurchaseOrderResponse> getAllPurchaseOrders();
    List<PurchaseOrderResponse> getPurchaseOrdersByStatus(OrderStatus status);
    PurchaseOrderResponse updateOrderStatus(Long id, OrderStatus status);
    void deletePurchaseOrder(Long id);

    // Called internally when low stock or forecast triggers reorder
    void autoCreatePurchaseOrder(Product product);
}