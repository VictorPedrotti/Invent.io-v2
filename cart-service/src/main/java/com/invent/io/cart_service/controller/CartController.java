package com.invent.io.cart_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invent.io.cart_service.model.Cart;
import com.invent.io.cart_service.model.CartItem;
import com.invent.io.cart_service.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
  
  private final CartService cartService;

  @GetMapping
  public ResponseEntity<Cart> getCart(String userId) {
    return cartService.getCartByUserId(userId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/add")
    public ResponseEntity<Cart> addItem(String userId, @RequestBody CartItem item) {
        Cart cart = cartService.addItemToCart(userId, item);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
