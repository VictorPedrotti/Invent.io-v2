package com.invent.io.procurement_service.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.invent.io.procurement_service.dto.PurchaseCompletedEventDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurchaseProducer {
  
  final RabbitTemplate rabbitTemplate;

  @Value(value = "${broker.queue.name}")
  private String routingKey;

  public void sendPurchaseMessage(PurchaseCompletedEventDto message) {
    rabbitTemplate.convertAndSend("", routingKey, message);
  }

}
