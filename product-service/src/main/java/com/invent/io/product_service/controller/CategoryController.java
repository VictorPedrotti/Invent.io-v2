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

import com.invent.io.product_service.dto.CategoryRequestDto;
import com.invent.io.product_service.dto.CategoryResponseDto;
import com.invent.io.product_service.model.Category;
import com.invent.io.product_service.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
  
  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable String id) {
    return ResponseEntity.ok(CategoryResponseDto.from(categoryService.getCategoryById(id)));
  }

  @PostMapping
  public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody @Valid CategoryRequestDto requestDto) {
    Category category = categoryService.createCategory(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponseDto.from(category));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable String id, @RequestBody CategoryRequestDto requestDto) {
    return ResponseEntity.ok(CategoryResponseDto.from(categoryService.updateCategory(id, requestDto)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }
}