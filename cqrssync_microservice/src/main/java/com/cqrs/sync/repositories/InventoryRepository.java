package com.cqrs.sync.repositories;

import com.cqrs.sync.entities.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	Iterable<Inventory> findByItem(String item);
}
