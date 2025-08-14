package com.invent.io.api_gateway.routes;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {
  
  @Value("${product.service.url}")
  private String productServiceUrl;

  @Value("${cart.service.url}")
  private String cartServiceUrl;

  @Value("${stock.service.url}")
  private String stockServiceUrl;

  @Value("${procurement.service.url}")
  private String procurementServiceUrl;

  @Bean
  public RouterFunction<ServerResponse> productServiceRoute() {
    return GatewayRouterFunctions.route("product_service")
              .route(RequestPredicates.path("/api/v1/products/**"), HandlerFunctions.http(URI.create(productServiceUrl)))
              .route(RequestPredicates.path("/api/v1/categories/**"), HandlerFunctions.http(URI.create(productServiceUrl)))
              .build();
  }

  @Bean
  public RouterFunction<ServerResponse> cartServiceRoute() {
    return GatewayRouterFunctions.route("cart_service")
              .route(RequestPredicates.path("/api/v1/carts/**"), HandlerFunctions.http(URI.create(cartServiceUrl)))
              .build();
  }

  @Bean
  public RouterFunction<ServerResponse> stockServiceRoute() {
    return GatewayRouterFunctions.route("stock_service")
              .route(RequestPredicates.path("/api/v1/stocks/**"), HandlerFunctions.http(URI.create(stockServiceUrl)))
              .route(RequestPredicates.path("/api/v1/stock-movements/**"), HandlerFunctions.http(URI.create(stockServiceUrl)))
              .build();
  }

  @Bean
  public RouterFunction<ServerResponse> procurementServiceRoute() {
    return GatewayRouterFunctions.route("stock_service")
              .route(RequestPredicates.path("/api/v1/suppliers/**"), HandlerFunctions.http(URI.create(procurementServiceUrl)))
              .route(RequestPredicates.path("/api/v1/purchases/**"), HandlerFunctions.http(URI.create(procurementServiceUrl)))
              .build();
  }

}
