package com.invent.io.procurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invent.io.procurement_service.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long>{

}
