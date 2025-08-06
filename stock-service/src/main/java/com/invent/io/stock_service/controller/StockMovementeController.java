package com.invent.io.stock_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invent.io.stock_service.model.StockMovement;
import com.invent.io.stock_service.services.StockMovementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stock-movements")
@RequiredArgsConstructor
public class StockMovementeController {
  
  private final StockMovementService stockMovementService;

  @GetMapping
  public ResponseEntity<List<StockMovement>> getMovementsBySku(@RequestParam String skuCode) {
    List<StockMovement> movements = stockMovementService.getMovementsBySku(skuCode);
    return ResponseEntity.ok(movements);
  }
}
