package com.invent.io.procurement_service.model;

import java.time.LocalDateTime;

import com.invent.io.procurement_service.enums.States;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(unique = true, length = 14)
  private String cnpj;

  private String email;
  private String phone;
  private String address;
  private String city;
  
  @Enumerated(EnumType.STRING)
  @Column(length = 2)
  private States state;

  @Column(length = 9)
  private String zipCode;

  private String contactPerson;
  private boolean isActive;

  @Column(nullable = true)
  private LocalDateTime deletedAt;
}
