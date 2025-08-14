package com.invent.io.procurement_service.dto;

import org.hibernate.validator.constraints.br.CNPJ;

import com.invent.io.procurement_service.enums.States;
import com.invent.io.procurement_service.model.Supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SupplierRequestDto(
  @NotBlank(message = "Nome é obrigatório")
  @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
  String name,

  @NotBlank(message = "CNPJ é obrigatório")
  @CNPJ(message = "CNPJ inválido")
  String cnpj,

  @NotBlank(message = "Email é obrigatório")
  @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
  @Email(message = "Email inválido")
  String email,

  @NotBlank(message = "Telefone é obrigatório")
  @Pattern(
    regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$",
    message = "Telefone inválido"
  )
  String phone,

  @NotBlank(message = "Endereço é obrigatório")
  @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
  String address,

  @NotBlank(message = "Cidade é obrigatória")
  @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
  String city,

  @NotNull(message = "Estado é obrigatório")
  States state,
  
  @NotBlank(message = "CEP é obrigatório")
  @Pattern(
    regexp = "^\\d{5}-?\\d{3}$",
    message = "CEP inválido"
  )
  String zipCode,

  @NotBlank(message = "Pessoa de contato é obrigatória")
  @Size(max = 100, message = "Pessoa de contato deve ter no máximo 100 caracteres")
  String contactPerson

) {

  public Supplier toEntity() {
    Supplier supplier = new Supplier();
    supplier.setName(name);
    supplier.setCnpj(cnpj);
    supplier.setEmail(email);
    supplier.setPhone(phone);
    supplier.setAddress(address);
    supplier.setCity(city);
    supplier.setState(state);
    supplier.setZipCode(zipCode);
    supplier.setContactPerson(contactPerson);
    supplier.setActive(true);
    return supplier;
  }
}
