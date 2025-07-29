package com.invent.io.cart_service.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
  
  private String productId; 
  private String name; 
  private int quantity; 
  private BigDecimal price; 
  private BigDecimal totalPrice; 
}
