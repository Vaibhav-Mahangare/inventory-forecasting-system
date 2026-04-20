package com.aiforecasting.inventory.inventory_forecasting_system.service;

import com.aiforecasting.inventory.inventory_forecasting_system.dto.ml.*;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Forecast;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Sale;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.ForecastRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.InventoryRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.ProductRepository;
import com.aiforecasting.inventory.inventory_forecasting_system.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MLForecastService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ForecastRepository forecastRepository;
    private final InventoryRepository inventoryRepository;
    private final PurchaseOrderService purchaseOrderService;
    private final RestTemplate restTemplate;

    @Value("${ml.forecast.api.url:http://localhost:8000/predict}")
    private String mlApiUrl;

    @Value("${ml.forecast.batch.url:http://localhost:8000/predict/batch}")
    private String mlBatchApiUrl;

    // -------------------------------------------------------
    // Single Product Forecast
    // -------------------------------------------------------
    public MLForecastResult getForecastForProduct(Long productId) {
        return getForecastForProduct(productId, false);
    }

    public MLForecastResult getForecastForProduct(Long productId, boolean forceRetrain) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + productId));

        // Fetch last 30 days of sales
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        List<Sale> recentSales = saleRepository
                .findByProductAndSaleDateBetween(product, startDate, endDate);

        if (recentSales.isEmpty()) {
            throw new RuntimeException(
                    "No sales data found for product: " + productId +
                            " in the last 30 days");
        }

        // Build ML request
        List<SaleDataPoint> salesDataPoints = recentSales.stream()
                .map(sale -> new SaleDataPoint(
                        sale.getSaleDate().toString(),
                        sale.getQuantitySold()
                ))
                .collect(Collectors.toList());

        MLForecastRequest mlRequest = new MLForecastRequest(
                productId, salesDataPoints, forceRetrain);

        // Call Python FastAPI
        log.info("Calling ML API for productId: {}, forceRetrain: {}",
                productId, forceRetrain);

        MLForecastResult result = restTemplate.postForObject(
                mlApiUrl,
                mlRequest,
                MLForecastResult.class
        );

        if (result == null) {
            throw new RuntimeException(
                    "ML API returned null response for product: " + productId);
        }

        // Save forecast to DB
        saveForecast(product, result);

        // Check if reorder needed based on forecast
        triggerReorderIfNeeded(product, result);

        log.info("Forecast complete for product: {} | " +
                        "7d:{} 15d:{} 30d:{} | MAPE:{}% | Accuracy:{}",
                productId,
                result.getPredicted7Days(),
                result.getPredicted15Days(),
                result.getPredicted30Days(),
                result.getMape(),
                result.getAccuracyLabel());

        return result;
    }

    // -------------------------------------------------------
    // Batch Forecast — All products at once
    // -------------------------------------------------------
    public List<MLForecastResult> getForecastForAllProducts() {
        List<Product> allProducts = productRepository.findAll();

        if (allProducts.isEmpty()) {
            throw new RuntimeException("No products found in the system");
        }

        // Build batch request — only include products that have sales data
        List<MLForecastRequest> productRequests = allProducts.stream()
                .map(product -> {
                    LocalDate endDate = LocalDate.now();
                    LocalDate startDate = endDate.minusDays(30);

                    List<Sale> recentSales = saleRepository
                            .findByProductAndSaleDateBetween(
                                    product, startDate, endDate);

                    List<SaleDataPoint> salesDataPoints = recentSales.stream()
                            .map(sale -> new SaleDataPoint(
                                    sale.getSaleDate().toString(),
                                    sale.getQuantitySold()
                            ))
                            .collect(Collectors.toList());

                    return new MLForecastRequest(
                            product.getProductId(), salesDataPoints, false);
                })
                .filter(req -> !req.getSalesData().isEmpty())
                .collect(Collectors.toList());

        if (productRequests.isEmpty()) {
            throw new RuntimeException(
                    "No products have sufficient sales data for forecasting");
        }

        // Build batch request wrapper
        MLBatchForecastRequest batchRequest =
                new MLBatchForecastRequest(productRequests);

        log.info("Calling ML Batch API for {} products", productRequests.size());

        // Call Python FastAPI batch endpoint
        MLBatchForecastResponse batchResponse = restTemplate.postForObject(
                mlBatchApiUrl,
                batchRequest,
                MLBatchForecastResponse.class
        );

        if (batchResponse == null || batchResponse.getResults() == null) {
            throw new RuntimeException("ML Batch API returned null response");
        }

        // Save each forecast and trigger reorder checks
        batchResponse.getResults().forEach(result -> {
            productRepository.findById(result.getProductId())
                    .ifPresent(product -> {
                        saveForecast(product, result);
                        triggerReorderIfNeeded(product, result);
                    });
        });

        log.info("Batch forecast complete | Total:{} Success:{} Failed:{}",
                batchResponse.getTotalProducts(),
                batchResponse.getSuccessful(),
                batchResponse.getFailed());

        return batchResponse.getResults();
    }

    // -------------------------------------------------------
    // Helper — Save forecast result to DB
    // -------------------------------------------------------
    private void saveForecast(Product product, MLForecastResult result) {
        Forecast forecast = new Forecast();
        forecast.setProduct(product);
        forecast.setPredictedQuantity(result.getPredicted30Days());
        forecast.setForecastDate(LocalDate.now().plusDays(30));
        forecast.setModelVersion("prophet-v2.0-" + result.getAccuracyLabel());
        forecastRepository.save(forecast);
    }

    // -------------------------------------------------------
    // Helper — Auto trigger reorder if forecast > current stock
    // -------------------------------------------------------
    private void triggerReorderIfNeeded(Product product, MLForecastResult result) {
        int totalStock = inventoryRepository.findByProduct(product)
                .stream()
                .mapToInt(i -> i.getQuantity())
                .sum();

        if (result.getPredicted30Days() > totalStock) {
            log.info("Forecast exceeds stock for product: {} | " +
                            "Forecast:{} Stock:{} — triggering auto reorder",
                    product.getProductId(), result.getPredicted30Days(), totalStock);
            purchaseOrderService.autoCreatePurchaseOrder(product);
        }
    }
}