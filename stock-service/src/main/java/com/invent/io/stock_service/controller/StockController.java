package com.invent.io.stock_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invent.io.stock_service.dto.StockRequestDto;
import com.invent.io.stock_service.dto.StockResponseDto;
import com.invent.io.stock_service.services.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {
  
  private final StockService stockService;

  @GetMapping
  public ResponseEntity<Boolean> checkStockAvailability(@RequestParam String skuCode, @RequestParam Integer quantity) {
    return ResponseEntity.ok(stockService.isInStock(skuCode, quantity));
  }

  @PostMapping("/add")
  public ResponseEntity<StockResponseDto> addStock(@RequestBody StockRequestDto dto) {
    return ResponseEntity.ok(StockResponseDto.from(stockService.addStock(dto.skuCode(), dto.quantity())));
  }

  @PostMapping("/decrement")
  public ResponseEntity<StockResponseDto> removeStock(@RequestBody StockRequestDto dto) {
    return ResponseEntity.ok(StockResponseDto.from(stockService.decrementStock(dto.skuCode(), dto.quantity())));
  }

  @PostMapping("/reserve")
  public ResponseEntity<StockResponseDto> reserveStock(@RequestBody StockRequestDto dto) {
    return ResponseEntity.ok(StockResponseDto.from(stockService.reserveStock(dto.skuCode(), dto.quantity())));
  }

  @PostMapping("/release")
  public ResponseEntity<StockResponseDto> releaseStock(@RequestBody StockRequestDto dto) {
    return ResponseEntity.ok(StockResponseDto.from(stockService.releaseStock(dto.skuCode(), dto.quantity())));
  }
}  
