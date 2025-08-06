package com.invent.io.cart_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.invent.io.cart_service.client.StockClient;

@Configuration
public class RestClientConfig {
  
  @Value("${stock.service.url}")
  private String stockServiceUrl;

  @Bean
  public StockClient stockClient(RestTemplateBuilder restTemplateBuilder) {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(3000);
    factory.setReadTimeout(3000);

    var restClient = RestClient.builder()
        .baseUrl(stockServiceUrl)
        .requestFactory(factory)
        .build();
    
    var adapter = RestClientAdapter.create(restClient);
    var proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
    return proxyFactory.createClient(StockClient.class);
  }
}
