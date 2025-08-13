package com.invent.io.procurement_service.dto;

import java.util.List;

import com.invent.io.procurement_service.model.Purchase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PurchaseRequestDto(
  @NotNull(message = "ID do fornecedor é obrigatório")
  Integer supplierId,

  @Valid
  List<PurchaseItemRequestDto> items
) {

  public Purchase toEntity() {
    Purchase purchase = new Purchase();
    return purchase;
  }
}
