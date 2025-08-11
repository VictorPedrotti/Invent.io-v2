package com.invent.io.procurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invent.io.procurement_service.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

}
