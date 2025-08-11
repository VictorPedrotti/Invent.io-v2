package com.invent.io.procurement_service.service;

import org.springframework.stereotype.Service;

import com.invent.io.procurement_service.dto.PurchaseRequestDto;
import com.invent.io.procurement_service.model.Purchase;
import com.invent.io.procurement_service.repository.PurchaseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseService {
  
  private final PurchaseRepository purchaseRepository;

  @Transactional
  public Purchase createPurchase(PurchaseRequestDto requestDto) {
    Purchase purchase = requestDto.toEntity();
    return purchaseRepository.save(purchase);
  }
}
