package com.invent.io.procurement_service.dto;

import java.math.BigDecimal;

import com.invent.io.procurement_service.model.PurchaseItem;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PurchaseItemRequestDto(
  @NotBlank(message = "ID do produto é obrigatório")
  String productId,

  @NotNull(message = "Quantidade é obrigatória")
  @Positive(message = "Quantidade deve ser positiva")
  Integer quantity,

  @Digits(integer = 10, fraction = 2, message = "Preço unitário deve ter no máximo 2 casas decimais")
  @Positive(message = "Preço unitário deve ser positivo")
  BigDecimal pricePerUnit,

  @NotBlank(message = "Código SKU é obrigatório")
  @Size(min = 2, max = 50, message = "Código SKU deve ter entre 2 e 50 caracteres")
  String skuCode
) {

  public PurchaseItem toEntity() {
    PurchaseItem purchaseItem = new PurchaseItem();
    purchaseItem.setProductId(productId);
    purchaseItem.setQuantity(quantity);
    purchaseItem.setPricePerUnit(pricePerUnit);
    purchaseItem.setSkuCode(skuCode);
    return purchaseItem;
  }
}
