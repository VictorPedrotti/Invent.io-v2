package com.invent.io.procurement_service.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItem {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "purchase_id", nullable = false)
  private Purchase purchase;

  private Integer productId;
  private Integer quantity;
  private BigDecimal pricePerUnit;
  private BigDecimal totalPrice;

  @Column(unique = true)
  private String skuCode;

}
