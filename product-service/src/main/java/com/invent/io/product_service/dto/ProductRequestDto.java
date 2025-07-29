package com.invent.io.product_service.dto;

import java.math.BigDecimal;

import com.invent.io.product_service.model.Product;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequestDto(
  @NotBlank(message = "Nome é obrigatório") 
  @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres") 
  String name,

  @Size(max = 255, message = "Descrição do produto não pode exceder 255 caracteres")
  String description,

  @Digits(integer = 10, fraction = 2, message = "Preço unitário deve ter no máximo 2 casas decimais")
  @Positive(message = "Preço unitário deve ser positivo")
  BigDecimal price,

  @NotBlank(message = "ID da categoria é obrigatório") 
  String categoryId,

  @NotBlank(message = "Código SKU é obrigatório")
  @Size(min = 2, max = 50, message = "Código SKU deve ter entre 2 e 50 caracteres")
  String skuCode,

  @Digits(integer = 10, fraction = 2, message = "Peso deve ter no máximo 2 casas decimais")
  @Positive(message = "Peso deve ser positivo")
  BigDecimal weight,

  @Size(max = 100, message = "Dimensões não pode exceder 50 caracteres")
  String dimensions,

  @Size(max = 255, message = "URL da imagem não pode exceder 255 caracteres")
  @NotBlank(message = "URL da imagem é obrigatória")
  String imageUrl,

  boolean isActive
) {

  public Product toEntity() {
    Product product = new Product();
    product.setName(name);
    product.setDescription(description);
    product.setPrice(price);
    product.setCategoryId(categoryId);
    product.setSkuCode(skuCode);
    product.setWeight(weight);
    product.setDimensions(dimensions);
    product.setImageUrl(imageUrl);
    product.setActive(isActive);
    return product;
  }

}
