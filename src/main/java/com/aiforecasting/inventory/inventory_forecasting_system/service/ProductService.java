package com.aiforecasting.inventory.inventory_forecasting_system.service;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.ProductRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(String category);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}