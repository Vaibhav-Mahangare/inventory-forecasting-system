package com.aiforecasting.inventory.inventory_forecasting_system.repository;

import com.aiforecasting.inventory.inventory_forecasting_system.entity.Forecast;
import com.aiforecasting.inventory.inventory_forecasting_system.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {

    List<Forecast> findByProduct(Product product);

    List<Forecast> findByProductAndForecastDateBetween(Product product, LocalDate start, LocalDate end);

    Optional<Forecast> findTopByProductOrderByForecastDateDesc(Product product);
}