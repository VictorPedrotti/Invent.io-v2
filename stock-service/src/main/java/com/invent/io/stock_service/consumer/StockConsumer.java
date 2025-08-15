package com.invent.io.stock_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.invent.io.stock_service.dto.PurchaseCompletedRequestDto;
import com.invent.io.stock_service.services.StockService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockConsumer {

  private final StockService stockService;

  @RabbitListener(queues = "${broker.queue.purchase.name}")
  public void consumePurchaseMessage(PurchaseCompletedRequestDto requestDto) {
    requestDto.items().forEach(item -> {
      stockService.addStock(item.skuCode(), item.quantity());
    });
  }
}
