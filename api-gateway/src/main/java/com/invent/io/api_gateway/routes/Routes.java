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

  @Bean
  public RouterFunction<ServerResponse> productServiceRoute() {
    return GatewayRouterFunctions.route("product_service")
              .route(RequestPredicates.path("/api/v1/products/**"), HandlerFunctions.http(URI.create(productServiceUrl)))
              .route(RequestPredicates.path("/api/v1/categories/**"), HandlerFunctions.http(URI.create(productServiceUrl)))
              .build();
  }

}
