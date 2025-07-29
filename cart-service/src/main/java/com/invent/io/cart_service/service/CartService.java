package com.invent.io.cart_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.invent.io.cart_service.model.Cart;
import com.invent.io.cart_service.model.CartItem;
import com.invent.io.cart_service.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
  
  private final CartRepository cartRepository;

  public Optional<Cart> getCartByUserId(String userId) {
    return cartRepository.findByUserId(userId);
  }

  public void clearCart(String userId) {
    cartRepository.deleteByUserId(userId);
  }

  public Cart addItemToCart(String userId, CartItem newItem) {
        Cart cart = cartRepository.findById(userId).orElseGet(() -> new Cart(userId, new ArrayList<>(), BigDecimal.ZERO));

        Optional<CartItem> existingItemOpt = cart.getItems()
            .stream()
            .filter(item -> item.getProductId().equals(newItem.getProductId()))
            .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
            existingItem.setTotalPrice(existingItem.getPrice().multiply(BigDecimal.valueOf(existingItem.getQuantity())));
        } else {
            newItem.setTotalPrice(newItem.getPrice().multiply(BigDecimal.valueOf(newItem.getQuantity())));
            cart.getItems().add(newItem);
        }

        cart.setTotal(cart.getItems().stream()
            .map(CartItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        return cartRepository.save(cart);
    }
}
