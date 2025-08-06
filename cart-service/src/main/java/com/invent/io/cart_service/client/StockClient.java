package com.invent.io.cart_service.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface StockClient {
  
  @GetExchange("/api/v1/stocks")
  Boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);
} 
