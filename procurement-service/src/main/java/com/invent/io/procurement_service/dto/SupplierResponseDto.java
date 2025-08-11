package com.invent.io.procurement_service.dto;

import com.invent.io.procurement_service.enums.States;
import com.invent.io.procurement_service.model.Supplier;

public record SupplierResponseDto(
  Long id,
  String name,
  String cnpj,
  String email,
  String phone,
  String address,
  String city,
  States state,
  String zipCode,
  String contactPerson,
  boolean isActive
) {

  public static SupplierResponseDto from(Supplier supplier) {
    return new SupplierResponseDto(
      supplier.getId(),
      supplier.getName(),
      supplier.getCnpj(),
      supplier.getEmail(),
      supplier.getPhone(),
      supplier.getAddress(),
      supplier.getCity(),
      supplier.getState(),
      supplier.getZipCode(),
      supplier.getContactPerson(),
      supplier.isActive()
    );
  }
}
