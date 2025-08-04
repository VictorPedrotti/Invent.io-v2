package com.invent.io.stock_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invent.io.stock_service.model.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

  List<StockMovement> findBySkuCode(String skuCode);
}
