package com.event.microservice.services;

import java.time.LocalDateTime;

import com.event.microservice.entities.EventStore;
import com.event.microservice.events.OrderCreatedEvent;
import com.event.microservice.events.OrderReversedEvent;
import com.event.microservice.events.PaymentFailureEvent;
import com.event.microservice.events.PaymentReversedEvent;
import com.event.microservice.events.PaymentSuccessEvent;
import com.event.microservice.events.ShipmentFailureEvent;
import com.event.microservice.events.ShipmentSuccessEvent;
import com.event.microservice.events.StockAddedEvent;
import com.event.microservice.events.StockFailureEvent;
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
	
	public void addEvent(StockFailureEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getStockDetails()));

		eventStore.setEventType("STOCK_FAILURE");
		
		eventStore.setEventOf(event.getStockDetails().getEventOf());

		eventStore.setEventTime(LocalDateTime.now());

		repo.save(eventStore);
	}
	public void addEvent(OrderCreatedEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getOrderDetails()));

		eventStore.setEventType("ORDER_ADDED");

		eventStore.setEventOf(event.getOrderDetails().getEventOf());

		eventStore.setEventTime(LocalDateTime.now());

		repo.save(eventStore);
	}
	
	public void addEvent(OrderReversedEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getOrderDetails()));

		eventStore.setEventType("ORDER_REMOVED");
		
		eventStore.setEventOf(event.getOrderDetails().getEventOf());

		eventStore.setEventTime(LocalDateTime.now());

		repo.save(eventStore);
	}

	public void addEvent(PaymentSuccessEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getPaymentDetails()));

		eventStore.setEventType("PAYMENT_SUCCESS");
		
		eventStore.setEventOf(event.getPaymentDetails().getEventOf());

		eventStore.setEventTime(LocalDateTime.now());

		repo.save(eventStore);
	}
	
	public void addEvent(PaymentFailureEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getPaymentDetails()));

		eventStore.setEventType("PAYMENT_FAILURE");
		
		eventStore.setEventOf(event.getPaymentDetails().getEventOf());

		eventStore.setEventTime(LocalDateTime.now());

		repo.save(eventStore);
	}
	
	public void addEvent(PaymentReversedEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getPaymentDetails()));

		eventStore.setEventType("PAYMENT_REVERSED");
		
		eventStore.setEventOf(event.getPaymentDetails().getEventOf());

		eventStore.setEventTime(LocalDateTime.now());

		repo.save(eventStore);
	}

	public void addEvent(ShipmentSuccessEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getShipmentDetails()));

		eventStore.setEventType("SHIPMENT_SUCCESS");
		
		eventStore.setEventOf(event.getShipmentDetails().getEventOf());

		eventStore.setEventTime(LocalDateTime.now());

		repo.save(eventStore);
	}
	
	public void addEvent(ShipmentFailureEvent event) throws JsonProcessingException {

		EventStore eventStore = new EventStore();

		eventStore.setEventData(new ObjectMapper().writeValueAsString(event.getShipmentDetails()));

		eventStore.setEventType("SHIPMENT_FAILURE");
		
		eventStore.setEventOf(event.getShipmentDetails().getEventOf());

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
