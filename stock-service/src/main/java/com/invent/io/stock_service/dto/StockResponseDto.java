package com.invent.io.stock_service.dto;

import com.invent.io.stock_service.model.Stock;

public record StockResponseDto(String skuCode, Integer quantityTotal, Integer quantityAvailable, Integer quantityReserved) {

    public static StockResponseDto from(Stock stock) {
        return new StockResponseDto(
            stock.getSkuCode(),
            stock.getQuantityTotal(),
            stock.getQuantityAvailable(),
            stock.getQuantityReserved()
        );
    }
}
