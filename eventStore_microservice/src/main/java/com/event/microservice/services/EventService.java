package com.event.microservice.services;

import java.time.LocalDateTime;

import com.event.microservice.entities.EventStore;
import com.event.microservice.events.StockAddedEvent;
import com.event.microservice.events.StockRemovedEvent;
import com.event.microservice.repositories.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

	@Autowired
	private EventRepository repo;

	public void addEvent(StockAddedEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getStockDetails()));

		eventStore.setEventType("STOCK_ADDED");

		eventStore.setEventOf(event.getStockDetails().getEventOf());

		eventStore.setEventTime(LocalDateTime.now());

		repo.save(eventStore);
	}

	public void addEvent(StockRemovedEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getStockDetails()));

		eventStore.setEventType("STOCK_REMOVED");
		eventStore.setEventOf(event.getStockDetails().getEventOf());

		eventStore.setEventTime(LocalDateTime.now());

		repo.save(eventStore);
	}

	public Iterable<EventStore> fetchAllEvents(String name) {

		return repo.findByEventOf(name);

	}

	public Iterable<EventStore> fetchAllEventsTillDate(String name, LocalDateTime date) {

		return repo.findByEventOfAndEventTimeLessThanEqual(name, date);

	}
}