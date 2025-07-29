package com.invent.io.cart_service.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Cart")
public class Cart {
  
  @Id
  private String userId; // UUID do Keycloak

  private List<CartItem> items = new ArrayList<>();
  private BigDecimal total;
}
