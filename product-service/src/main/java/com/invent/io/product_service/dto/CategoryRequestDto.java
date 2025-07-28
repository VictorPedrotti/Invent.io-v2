package com.invent.io.product_service.dto;

import com.invent.io.product_service.model.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(
  @NotBlank(message = "Nome é obrigatório") @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres") String name,
  @NotBlank(message = "Descrição é obrigatória") String description,
  String parentCategoryId
) {

  public Category toEntity() {
    Category category = new Category();
    category.setName(name);
    category.setDescription(description);
    category.setParentCategoryId(parentCategoryId);
    return category;  
  }

}
