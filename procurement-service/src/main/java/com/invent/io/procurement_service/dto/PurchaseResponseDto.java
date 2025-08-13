package com.invent.io.procurement_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.invent.io.procurement_service.enums.PurchaseStatus;
import com.invent.io.procurement_service.model.Purchase;

public record PurchaseResponseDto(
    Long id,
    Integer supplierId,
    BigDecimal totalAmount,
    PurchaseStatus status,
    LocalDateTime purchaseDate,
    LocalDateTime receivedAt,
    List<PurchaseItemResponseDto> items

) {

  public static PurchaseResponseDto from(Purchase purchase) {
    return new PurchaseResponseDto(
        purchase.getId(),
        purchase.getSupplierId(),
        purchase.getTotalAmount(),
        purchase.getStatus(),
        purchase.getPurchaseDate(),
        purchase.getReceivedAt(),
        purchase.getItems().stream()
            .map(PurchaseItemResponseDto::from)
            .toList());
  }
}
