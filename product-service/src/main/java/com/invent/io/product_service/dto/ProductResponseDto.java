package com.invent.io.product_service.dto;

import java.math.BigDecimal;

import com.invent.io.product_service.model.Product;

public record ProductResponseDto(
  String id, 
  String name, 
  String description,
  BigDecimal price, 
  String categoryId,
  String skuCode,
  BigDecimal weight,
  String dimensions,
  String imageUrl,
  boolean isActive 
  ) {

    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategoryId(),
            product.getSkuCode(),
            product.getWeight(),
            product.getDimensions(),
            product.getImageUrl(),
            product.isActive()
        );
    }

}
