package com.invent.io.procurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invent.io.procurement_service.model.PurchaseItem;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

}
