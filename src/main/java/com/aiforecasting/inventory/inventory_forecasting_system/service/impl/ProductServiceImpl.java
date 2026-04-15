package com.aiforecasting.inventory.inventory_forecasting_system.service.impl;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.request.ProductRequest;
import com.aiforecasting.inventory.inventory_forecasting_system.dto.response.ProductResponse;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.ProductRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setLeadTimeDays(request.getLeadTimeDays());
        product.setCreatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        return mapToResponse(saved);                        // converts entity into a response dto
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setLeadTimeDays(request.getLeadTimeDays());

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    // mapper
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setLeadTimeDays(product.getLeadTimeDays());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}