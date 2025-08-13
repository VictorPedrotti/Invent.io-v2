package com.invent.io.procurement_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.invent.io.procurement_service.dto.SupplierRequestDto;
import com.invent.io.procurement_service.dto.SupplierResponseDto;
import com.invent.io.procurement_service.model.Supplier;
import com.invent.io.procurement_service.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {
  
  private final SupplierRepository supplierRepository;

  public List<SupplierResponseDto> getAllSuppliers() {
    return supplierRepository.findAll().stream()
        .map(SupplierResponseDto::from)
        .toList();
  }

  public Supplier getSupplierById(Long id) {
    return supplierRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com id: " + id));
  }

  public Supplier createSupplier(SupplierRequestDto requestDtodto) {
    return supplierRepository.save(requestDtodto.toEntity());
  }

  public void deleteSupplier(Long id) {
    if (!supplierRepository.existsById(id)) {
      throw new RuntimeException("Fornecedor não encontrado com id: " + id);
    }

    Supplier supplier = getSupplierById(id);
    supplier.setDeletedAt(LocalDateTime.now());
    supplier.setActive(false);
    supplierRepository.save(supplier);
  }

  public Supplier updateSupplier(Long id, SupplierRequestDto requestDtodto) {
    return supplierRepository.findById(id)
        .map(supplier -> {
          supplier.setName(requestDtodto.name());
          supplier.setCnpj(requestDtodto.cnpj());
          supplier.setEmail(requestDtodto.email());
          supplier.setPhone(requestDtodto.phone());
          supplier.setAddress(requestDtodto.address());
          supplier.setCity(requestDtodto.city());
          supplier.setState(requestDtodto.state());
          supplier.setZipCode(requestDtodto.zipCode());
          supplier.setContactPerson(requestDtodto.contactPerson());
          supplier.setActive(true);
          supplier.setDeletedAt(null);
          return supplierRepository.save(supplier);
        })
        .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com id: " + id));
  }
}
