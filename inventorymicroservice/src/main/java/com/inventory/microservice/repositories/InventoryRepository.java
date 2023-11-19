package com.inventory.microservice.repositories;

import com.inventory.microservice.entities.Inventory;

import org.springframework.data.repository.CrudRepository;

public interface InventoryRepository extends CrudRepository<Inventory,Long>{


    Iterable<Inventory> findByItem(String item);
}
