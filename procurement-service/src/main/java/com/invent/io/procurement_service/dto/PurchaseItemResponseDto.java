package com.invent.io.procurement_service.dto;

import java.math.BigDecimal;

import com.invent.io.procurement_service.model.PurchaseItem;

public record PurchaseItemResponseDto(
    Long id,
    Integer productId,
    Integer quantity,
    BigDecimal pricerPerUnit,
    BigDecimal totalPrice,
    String skuCode) {

  public static PurchaseItemResponseDto from(PurchaseItem item) {
    return new PurchaseItemResponseDto(
        item.getId(),
        item.getProductId(),
        item.getQuantity(),
        item.getPricePerUnit(),
        item.getTotalPrice(),
        item.getSkuCode());
  }
}
