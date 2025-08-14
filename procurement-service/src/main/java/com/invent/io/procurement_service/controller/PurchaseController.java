package com.invent.io.procurement_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invent.io.procurement_service.dto.PurchaseRequestDto;
import com.invent.io.procurement_service.dto.PurchaseResponseDto;
import com.invent.io.procurement_service.dto.UpdatePurchaseStatusDto;
import com.invent.io.procurement_service.service.PurchaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {
  
  private final PurchaseService purchaseService;

  @GetMapping
  public ResponseEntity<List<PurchaseResponseDto>> getAllPurchases() {
    return ResponseEntity.ok().body(purchaseService.getAllPurchases());  
  }

  @GetMapping("/{id}")
  public ResponseEntity<PurchaseResponseDto> getPurchaseById(@PathVariable Long id) {
    return ResponseEntity.ok(PurchaseResponseDto.from(purchaseService.getPurchaseById(id)));
  }

  @PostMapping
  public ResponseEntity<PurchaseResponseDto> createPurchase(@RequestBody @Valid PurchaseRequestDto requestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(PurchaseResponseDto.from(purchaseService.createPurchase(requestDto)));
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<PurchaseResponseDto> updatePurchaseStatus(@PathVariable Long id, @RequestBody UpdatePurchaseStatusDto dto) {
    return ResponseEntity.ok(PurchaseResponseDto.from(purchaseService.updatePurchaseStatus(id, dto.status()))); 
  }
}
