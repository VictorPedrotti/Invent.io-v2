package com.invent.io.cart_service.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.invent.io.cart_service.model.Cart;

public interface CartRepository extends CrudRepository<Cart, String> {
  
  Optional<Cart> findByUserId(String userId);
  
  void deleteByUserId(String userId);
  
  boolean existsByUserId(String userId);

}
