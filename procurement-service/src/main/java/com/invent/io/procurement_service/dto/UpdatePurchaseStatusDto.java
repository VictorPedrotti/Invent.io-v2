package com.invent.io.procurement_service.dto;

import com.invent.io.procurement_service.enums.PurchaseStatus;

import jakarta.validation.constraints.NotNull;

public record UpdatePurchaseStatusDto(
  @NotNull(message = "O status da compra é obrigatório")
  PurchaseStatus status
) {

}
