package com.invent.io.product_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.invent.io.product_service.dto.ProductRequestDto;
import com.invent.io.product_service.dto.ProductResponseDto;
import com.invent.io.product_service.model.Product;
import com.invent.io.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
  
  private final ProductRepository productRepository;

  public List<ProductResponseDto> getAllProducts() {
    return productRepository.findAll().stream()
        .map(ProductResponseDto::from)
        .toList();
  }

  public Product getProductbyId(String id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
  }

  public Product createProduct(ProductRequestDto requestDto) {
    return productRepository.save(requestDto.toEntity());
  }

  public void deleteProduct(String id) {
    if (!productRepository.existsById(id)) {
      throw new RuntimeException("Produto não encontrado com id: " + id);
    }
    
    Product product = getProductbyId(id);
    product.setDeletedAt(LocalDateTime.now());
    productRepository.save(product);
  }

  public Product updateProduct(String id, ProductRequestDto requestDto) {
    return productRepository.findById(id)
        .map(product -> {
          product.setName(requestDto.name());
          product.setDescription(requestDto.description());
          product.setPrice(requestDto.price());
          product.setCategoryId(requestDto.categoryId());
          product.setSkuCode(requestDto.skuCode());
          product.setWeight(requestDto.weight());
          product.setDimensions(requestDto.dimensions());
          product.setImageUrl(requestDto.imageUrl());
          product.setActive(requestDto.isActive());
          return productRepository.save(product);
        })
        .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
  }

}
