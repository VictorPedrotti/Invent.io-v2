package com.invent.io.stock_service.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.invent.io.stock_service.model.StockMovement;
import com.invent.io.stock_service.repository.StockMovementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockMovementService {
  
  private final StockMovementRepository stockMovementRepository;

  public List<StockMovement> getMovementsBySku(String skuCode) {
        return stockMovementRepository.findBySkuCode(skuCode);
  }
  
}
