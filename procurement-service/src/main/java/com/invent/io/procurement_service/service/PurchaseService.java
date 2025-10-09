package com.invent.io.procurement_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.invent.io.procurement_service.dto.PurchaseCompletedEventDto;
import com.invent.io.procurement_service.dto.PurchaseRequestDto;
import com.invent.io.procurement_service.dto.PurchaseResponseDto;
import com.invent.io.procurement_service.enums.PurchaseStatus;
import com.invent.io.procurement_service.model.Purchase;
import com.invent.io.procurement_service.model.PurchaseItem;
import com.invent.io.procurement_service.producer.PurchaseProducer;
import com.invent.io.procurement_service.repository.PurchaseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseService {

  private final PurchaseRepository purchaseRepository;
  private final PurchaseProducer purchaseProducer;

  @Transactional
  public Purchase createPurchase(PurchaseRequestDto requestDto) {
    Purchase purchase = requestDto.toEntity();
    purchase.setStatus(PurchaseStatus.PENDING);
    purchase.setPurchaseDate(LocalDateTime.now());

    List<PurchaseItem> items = requestDto.items().stream()
        .map(itemDto -> {
          PurchaseItem item = itemDto.toEntity();
          item.setPurchase(purchase);
          item.setTotalPrice(item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity())));
          return item;
        }).toList();

    purchase.setItems(items);

    purchase.setTotalAmount(items.stream()
        .map(PurchaseItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add));

    return purchaseRepository.save(purchase);
  }

  public List<PurchaseResponseDto> getAllPurchases() {
    return purchaseRepository.findAll().stream()
        .map(PurchaseResponseDto::from)
        .toList();
  }

  public Purchase getPurchaseById(Long id) {
    return purchaseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Compra não encontrada com id: " + id));
  }

  @Transactional
  public Purchase updatePurchaseStatus(Long id, PurchaseStatus status) {
    Purchase purchase = purchaseRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Compra não encontrada com id: " + id));

    Set<PurchaseStatus> lockedStatuses = Set.of(PurchaseStatus.COMPLETED, PurchaseStatus.CANCELLED);

    if (lockedStatuses.contains(purchase.getStatus())) {
      throw new RuntimeException("Não é possível alterar o status da compra já finalizada ou cancelada.");
    }

    purchase.setStatus(status);

    if (status == PurchaseStatus.COMPLETED) {
      purchase.setReceivedAt(LocalDateTime.now());
    }

    Purchase savedPurchase = purchaseRepository.save(purchase);

    if (status == PurchaseStatus.COMPLETED) {
      try {
        List<PurchaseCompletedEventDto.Item> items = savedPurchase.getItems().stream()
            .map(item -> new PurchaseCompletedEventDto.Item(
                item.getSkuCode(),
                item.getQuantity()))
            .toList();

        PurchaseCompletedEventDto eventDto = new PurchaseCompletedEventDto(
            savedPurchase.getId(),
            items);

        purchaseProducer.sendPurchaseMessage(eventDto);
      } catch (Exception e) {
        throw new RuntimeException("Falha ao enviar mensagem para a fila", e);
      }
    }
    return savedPurchase;
  }

}
