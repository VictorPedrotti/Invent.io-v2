package com.invent.io.stock_service.dto;

import java.util.List;

public record PurchaseCompletedRequestDto(
  Long purchaseId,
  List<Item> items
) {
  public record Item(String skuCode, Integer quantity) {}
}
