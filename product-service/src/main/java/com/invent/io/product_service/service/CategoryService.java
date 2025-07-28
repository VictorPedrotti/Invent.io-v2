package com.invent.io.product_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.invent.io.product_service.dto.CategoryRequestDto;
import com.invent.io.product_service.dto.CategoryResponseDto;
import com.invent.io.product_service.model.Category;
import com.invent.io.product_service.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  
  public List<CategoryResponseDto> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(CategoryResponseDto::from)
        .toList();
  }

  public Category getCategoryById(String id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Categoria não encontrada com id: " + id));
  }

  public Category createCategory(CategoryRequestDto requestDto) {
    return categoryRepository.save(requestDto.toEntity());
  }

  public void deleteCategory(String id) {

    if(!categoryRepository.existsById(id)) {
      throw new RuntimeException("Categoria não encontrada com id: " + id);
    }
    
    Category category = getCategoryById(id);
    category.setDeletedAt(LocalDateTime.now());
    categoryRepository.save(category);
  }

  public Category updateCategory(String id, CategoryRequestDto requestDto) {
    return categoryRepository.findById(id)
        .map(category -> {
          category.setName(requestDto.name());
          category.setDescription(requestDto.description());
          category.setParentCategoryId(requestDto.parentCategoryId());
          return categoryRepository.save(category);
        })
        .orElseThrow(() -> new RuntimeException("Categoria não encontrada com id: " + id));
  }
}
