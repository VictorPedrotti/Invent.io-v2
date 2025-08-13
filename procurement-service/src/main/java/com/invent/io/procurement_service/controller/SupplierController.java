package com.invent.io.procurement_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invent.io.procurement_service.dto.SupplierRequestDto;
import com.invent.io.procurement_service.dto.SupplierResponseDto;
import com.invent.io.procurement_service.service.SupplierService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

  private final SupplierService supplierService;

  @GetMapping
  public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers() {
    return ResponseEntity.ok(supplierService.getAllSuppliers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable Long id) {
    return ResponseEntity.ok(SupplierResponseDto.from(supplierService.getSupplierById(id)));
  }

  @PostMapping
  public ResponseEntity<SupplierResponseDto> createSupplier(@RequestBody @Valid SupplierRequestDto requestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(SupplierResponseDto.from(supplierService.createSupplier(requestDto)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<SupplierResponseDto> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierRequestDto requestDto) {
    return ResponseEntity.ok(SupplierResponseDto.from(supplierService.updateSupplier(id, requestDto)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
    supplierService.deleteSupplier(id);
    return ResponseEntity.noContent().build(); 
  }
}
