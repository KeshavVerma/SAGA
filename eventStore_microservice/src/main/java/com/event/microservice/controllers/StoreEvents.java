package com.event.microservice.controllers;

import javax.transaction.Transactional;

import com.event.microservice.dto.CustomerOrder;
import com.event.microservice.dto.Payment;
import com.event.microservice.dto.Shipment;
import com.event.microservice.dto.Stock;
import com.event.microservice.events.InventoryEvent;
import com.event.microservice.events.OrderCreatedEvent;
import com.event.microservice.events.OrderEvent;
import com.event.microservice.events.OrderReversedEvent;
import com.event.microservice.events.PaymentEvent;
import com.event.microservice.events.PaymentFailureEvent;
import com.event.microservice.events.PaymentReversedEvent;
import com.event.microservice.events.PaymentSuccessEvent;
import com.event.microservice.events.ShipmentFailureEvent;
import com.event.microservice.events.ShipmentSuccessEvent;
import com.event.microservice.events.StockAddedEvent;
import com.event.microservice.events.StockFailureEvent;
import com.event.microservice.events.StockRemovedEvent;
import com.event.microservice.services.EventService;
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
			// if stock added by URL request
			if (inventoryEvent.getOrder() == null) {
				stock = inventoryEvent.getStock();
				StockAddedEvent eventRec = StockAddedEvent.builder().stockDetails(stock).build();
				eventService.addEvent(eventRec);
			} else {
				stock = new Stock();
				stock.setItem(inventoryEvent.getOrder().getItem());
				stock.setQuantity(inventoryEvent.getOrder().getQuantity());
				stock.setOrderId(inventoryEvent.getOrder().getOrderId());
				stock.setUser(inventoryEvent.getOrder().getUser());
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
			stock.setUser(inventoryEvent.getOrder().getUser());
			StockAddedEvent eventRec = StockAddedEvent.builder().stockDetails(stock).build();
			eventService.addEvent(eventRec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@KafkaListener(topics = "shipment-success", groupId = "shipsuccess-group")
	public void shipmentSuccessEvent(String event) {

		try {
			InventoryEvent inventoryEvent = new ObjectMapper().readValue(event, InventoryEvent.class);
			Shipment shipment = new Shipment();
			shipment.setOrderId(inventoryEvent.getOrder().getOrderId());
			shipment.setAddress(inventoryEvent.getOrder().getAddress());
			ShipmentSuccessEvent eventShip = ShipmentSuccessEvent.builder().shipmentDetails(shipment).build();
			eventService.addEvent(eventShip);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@KafkaListener(topics = "new-orders", groupId = "createorder-group")
	public void addOrderEvent(String event) {

		try {
			OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);
			CustomerOrder order;
			order = orderEvent.getOrder();
			OrderCreatedEvent orderEventRec = OrderCreatedEvent.builder().orderDetails(order).build();
			eventService.addEvent(orderEventRec);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Transactional
	@KafkaListener(topics = "reversed-orders", groupId = "reversorders-group")
	public void reverseOrderEvent(String event) {

		try {
			OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);
			CustomerOrder order;
			order = orderEvent.getOrder();
			OrderReversedEvent reverseorderEvent = OrderReversedEvent.builder().orderDetails(order).build();
			eventService.addEvent(reverseorderEvent);

			if (order.getFailIn().equals("PAYMENT")) {
				Payment payment = new Payment();
				payment.setAmount(order.getAmount());
				payment.setMode(order.getPaymentMode());
				payment.setOrderId(order.getOrderId());
				payment.setUser(order.getUser());
				PaymentFailureEvent paymentFailureEvent = PaymentFailureEvent.builder().paymentDetails(payment).build();
				eventService.addEvent(paymentFailureEvent);
			} else if (order.getFailIn().equals("INVENTORY")) {
				Stock stock = new Stock();
				stock.setItem(order.getItem());
				stock.setQuantity(order.getQuantity());
				stock.setOrderId(order.getOrderId());
				stock.setUser(order.getUser());
				StockFailureEvent eventRec = StockFailureEvent.builder().stockDetails(stock).build();
				eventService.addEvent(eventRec);
			} else if (order.getFailIn().equals("SHIPMENT")) {
				Shipment shipment = new Shipment();
				shipment.setOrderId(order.getOrderId());
				shipment.setAddress(order.getAddress());
				ShipmentFailureEvent eventShip = ShipmentFailureEvent.builder().shipmentDetails(shipment).build();
				eventService.addEvent(eventShip);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@KafkaListener(topics = "new-payments", groupId = "newpayments-group")
	public void updateInventory(String event) {

		try {
			PaymentEvent paymentEvent = new ObjectMapper().readValue(event, PaymentEvent.class);
			CustomerOrder order;
			order = paymentEvent.getOrder();
			Payment payment = new Payment();
			payment.setAmount(order.getAmount());
			payment.setMode(order.getPaymentMode());
			payment.setOrderId(order.getOrderId());
			payment.setUser(order.getUser());
			PaymentSuccessEvent paymentSuccessEvent = PaymentSuccessEvent.builder().paymentDetails(payment).build();
			eventService.addEvent(paymentSuccessEvent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@KafkaListener(topics = "reversed-payments", groupId = "revpayments-group")
	public void reversePayment(String event) {

		try {
			PaymentEvent paymentEvent = new ObjectMapper().readValue(event, PaymentEvent.class);
			CustomerOrder order;
			order = paymentEvent.getOrder();
			Payment payment = new Payment();
			payment.setAmount(order.getAmount());
			payment.setMode(order.getPaymentMode());
			payment.setOrderId(order.getOrderId());
			payment.setUser(order.getUser());
			PaymentReversedEvent paymentReversedEvent = PaymentReversedEvent.builder().paymentDetails(payment).build();
			eventService.addEvent(paymentReversedEvent);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
