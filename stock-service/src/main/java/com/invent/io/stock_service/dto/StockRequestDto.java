package com.invent.io.stock_service.dto;

import com.invent.io.stock_service.model.Stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StockRequestDto(
  @NotBlank(message = "SKU do produto é obrigatório") String skuCode,
  @NotNull(message = "Quantidade é obrigatória") @Min(value = 1, message = "Quantidade deve ser no mínimo 1") Integer quantity
) {

  public Stock toEntity() {
    Stock stock = new Stock();
    stock.setSkuCode(skuCode);
    stock.setQuantityTotal(quantity);
    stock.setQuantityAvailable(quantity);
    stock.setQuantityReserved(0);
    return stock;
  }
}
