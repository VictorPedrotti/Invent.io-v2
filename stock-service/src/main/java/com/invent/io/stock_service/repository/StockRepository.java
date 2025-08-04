package com.invent.io.stock_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invent.io.stock_service.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
  Optional<Stock> findBySkuCode(String skuCode);

  boolean existsBySkuCodeAndQuantityAvailableIsGreaterThanEqual(String skuCode, int quantity);
}
