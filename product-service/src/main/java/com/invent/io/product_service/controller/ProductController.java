package com.invent.io.product_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invent.io.product_service.dto.ProductRequestDto;
import com.invent.io.product_service.dto.ProductResponseDto;
import com.invent.io.product_service.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
  
  private final ProductService productService;

  @GetMapping
  public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
    return ResponseEntity.ok(productService.getAllProducts());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String id) {
    return ResponseEntity.ok(ProductResponseDto.from(productService.getProductbyId(id)));
  }

  @PostMapping
  public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid ProductRequestDto requestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponseDto.from(productService.createProduct(requestDto)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable String id, @RequestBody ProductRequestDto requestDto) {
    return ResponseEntity.ok(ProductResponseDto.from(productService.updateProduct(id, requestDto)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}