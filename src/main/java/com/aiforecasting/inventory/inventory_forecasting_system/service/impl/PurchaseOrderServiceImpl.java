package com.aiforecasting.inventory.inventory_forecasting_system.service.impl;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.PurchaseOrderRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.PurchaseOrderResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.PurchaseOrder;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Supplier;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.type.OrderStatus;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.ProductRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.PurchaseOrderRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.SupplierRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + request.getSupplierId()));

        PurchaseOrder order = new PurchaseOrder();
        order.setQuantity(request.getQuantity());
        order.setOrderDate(request.getOrderDate());
        order.setExpectedDelivery(request.getExpectedDelivery());
        order.setStatus(request.getStatus());
        order.setProduct(product);
        order.setSupplier(supplier);

        PurchaseOrder saved = purchaseOrderRepository.save(order);
        return mapToResponse(saved);
    }

    @Override
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseOrder not found with id: " + id));
        return mapToResponse(order);
    }

    @Override
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrderResponse> getPurchaseOrdersByStatus(OrderStatus status) {
        return purchaseOrderRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseOrderResponse updateOrderStatus(Long id, OrderStatus status) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseOrder not found with id: " + id));
        order.setStatus(status);
        PurchaseOrder updated = purchaseOrderRepository.save(order);
        return mapToResponse(updated);
    }

    @Override
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchaseOrder not found with id: " + id));
        purchaseOrderRepository.delete(order);
    }

    // -------------------------------------------------------
    // Core Business Logic — Auto Purchase Order Creation
    // Only creates if no PENDING order already exists
    // Picks the first available supplier for the product
    // -------------------------------------------------------
    @Override
    public void autoCreatePurchaseOrder(Product product) {
        boolean pendingExists = purchaseOrderRepository
                .existsByProductAndStatus(product, OrderStatus.PENDING);

        if (!pendingExists) {
            // Pick first available supplier
            Supplier supplier = supplierRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No supplier available for auto purchase order"));

            PurchaseOrder order = new PurchaseOrder();
            order.setProduct(product);
            order.setSupplier(supplier);
            order.setQuantity(product.getLeadTimeDays() * 10); // default quantity formula
            order.setOrderDate(LocalDate.now());
            order.setExpectedDelivery(LocalDate.now().plusDays(product.getLeadTimeDays()));
            order.setStatus(OrderStatus.PENDING);

            purchaseOrderRepository.save(order);
        }
    }

    private PurchaseOrderResponse mapToResponse(PurchaseOrder order) {
        PurchaseOrderResponse response = new PurchaseOrderResponse();
        response.setOrderId(order.getOrderId());
        response.setQuantity(order.getQuantity());
        response.setOrderDate(order.getOrderDate());
        response.setExpectedDelivery(order.getExpectedDelivery());
        response.setStatus(order.getStatus());
        response.setProductId(order.getProduct().getProductId());
        response.setProductName(order.getProduct().getName());
        response.setSupplierId(order.getSupplier().getSupplierId());
        response.setSupplierName(order.getSupplier().getName());
        return response;
    }
}