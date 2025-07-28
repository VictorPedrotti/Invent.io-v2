package com.invent.io.product_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.invent.io.product_service.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {}
