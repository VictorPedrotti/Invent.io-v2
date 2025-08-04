package com.invent.io.stock_service.model;

import java.time.LocalDateTime;

import com.invent.io.stock_service.enums.MovementType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockMovement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String skuCode;
  private LocalDateTime timestamp;
  private MovementType type;
  private Integer quantity;
  private String description;
  
}
