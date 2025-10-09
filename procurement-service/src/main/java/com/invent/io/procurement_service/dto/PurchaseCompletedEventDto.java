package com.invent.io.procurement_service.dto;

import java.util.List;

public record PurchaseCompletedEventDto(
  Long purchaseId,
  List<Item> items
) {
  public record Item(String skuCode, Integer quantity) {}
}
