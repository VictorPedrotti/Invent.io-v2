package com.invent.io.product_service.dto;

import com.invent.io.product_service.model.Category;

public record CategoryResponseDto(String id, String name, String description, String parentCategoryId) {

    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getParentCategoryId()
        );
    }
}
