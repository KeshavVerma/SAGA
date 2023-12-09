package com.event.microservice.controllers;

import com.event.microservice.dto.Stock;
import com.event.microservice.events.InventoryEvent;
import com.event.microservice.events.StockAddedEvent;
import com.event.microservice.events.StockRemovedEvent;
import com.event.microservice.services.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StoreEvents {

//	@Autowired
//	private ObjectMapper objectMapper;

	@Autowired
	private EventService eventService;

	@KafkaListener(topics = { "new-inventory", "update-inventory" }, groupId = "addinventory-group")
	public void addInvetoryEvent(String event) {

		try {
			InventoryEvent inventoryEvent = new ObjectMapper().readValue(event, InventoryEvent.class);
			Stock stock;
			if (inventoryEvent.getOrder() == null) {
				stock = inventoryEvent.getStock();
				stock.setUser("KK");
				StockAddedEvent eventRec = StockAddedEvent.builder().stockDetails(stock).build();
				eventService.addEvent(eventRec);
			} else {
				stock = new Stock();
				stock.setItem(inventoryEvent.getOrder().getItem());
				stock.setQuantity(inventoryEvent.getOrder().getQuantity());
				stock.setUser("KK");
				StockRemovedEvent eventRec = StockRemovedEvent.builder().stockDetails(stock).build();
				eventService.addEvent(eventRec);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@KafkaListener(topics = "reversed-inventory", groupId = "revinventory-group")
	public void revInvetoryEvent(String event) {

		try {
			InventoryEvent inventoryEvent = new ObjectMapper().readValue(event, InventoryEvent.class);
			Stock stock = new Stock();
			stock.setItem(inventoryEvent.getOrder().getItem());
			stock.setQuantity(inventoryEvent.getOrder().getQuantity());
			stock.setUser("KK");
			StockAddedEvent eventRec = StockAddedEvent.builder().stockDetails(stock).build();
			eventService.addEvent(eventRec);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
