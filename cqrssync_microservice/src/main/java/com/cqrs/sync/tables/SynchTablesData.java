package com.cqrs.sync.tables;

import javax.transaction.Transactional;

import com.cqrs.sync.entities.Inventory;
import com.cqrs.sync.events.InventoryEvent;
import com.cqrs.sync.repositories.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SynchTablesData {

	@Autowired
	InventoryRepository repository;

	@Transactional
	@KafkaListener(topics = "new-inventory", groupId = "newinventory-group")
	public void syncInventoryTables(String event) {

		System.out.println("***** 1" + event);
		
		try {
			InventoryEvent inventoryEvent = new ObjectMapper().readValue(event, InventoryEvent.class);
			Inventory i = new Inventory();
			i.setItem(inventoryEvent.getStock().getItem());
			i.setQuantity(inventoryEvent.getStock().getQuantity());
			this.repository.save(i);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Transactional
	@KafkaListener(topics = "update-inventory", groupId = "updateinventory-group")
	public void syncInvetoryTables(String event) {

		System.out.println("***** 2" + event);
		
		try {
			InventoryEvent inventoryEvent = new ObjectMapper().readValue(event, InventoryEvent.class);
			
			if (inventoryEvent.getOrder() == null) {
				//if inventory directly updated by inventory URL for same product 
				Iterable<Inventory> items = this.repository.findByItem(inventoryEvent.getStock().getItem());

				if (items.iterator().hasNext()) {

					items.forEach(i -> {

						i.setQuantity(inventoryEvent.getStock().getQuantity() + i.getQuantity());
						this.repository.save(i);
					});
				}
			} else {
				//if inventory updated by orders of item
				Iterable<Inventory> items = this.repository.findByItem(inventoryEvent.getOrder().getItem());
				if (items.iterator().hasNext()) {
					
					items.forEach(i -> {
						i.setQuantity(i.getQuantity() - inventoryEvent.getOrder().getQuantity());
						this.repository.save(i);
					});
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	@KafkaListener(topics = "reversed-inventory", groupId = "reversinventory-group")
	public void syncRInvetoryTables(String event) {

		System.out.println("***** 3" + event);
		
		try {
			InventoryEvent inventoryEvent = new ObjectMapper().readValue(event, InventoryEvent.class);
			
			if (inventoryEvent.getOrder() != null) {
				
				Iterable<Inventory> items = this.repository.findByItem(inventoryEvent.getOrder().getItem());
				if (items.iterator().hasNext()) {
					
					items.forEach(i -> {
						i.setQuantity(i.getQuantity() + inventoryEvent.getOrder().getQuantity());
						this.repository.save(i);
					});
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
