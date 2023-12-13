package com.event.microservice.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.event.microservice.dto.CustomerOrder;
import com.event.microservice.dto.Shipment;
import com.event.microservice.dto.Stock;
import com.event.microservice.entities.EventStore;
import com.event.microservice.services.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@GetMapping("/events")
	public Iterable<EventStore> getEvents(@RequestParam("name") String name) throws JsonProcessingException {

		Iterable<EventStore> events = eventService.fetchAllEvents(name);

		return events;

	}
	
	
	@GetMapping("/events/{event}")
	public Iterable<EventStore> getEventsFor(@PathVariable("name") String name) throws JsonProcessingException {

		Iterable<EventStore> events = eventService.fetchAllEvents(name);

		return events;

	}
	
	@GetMapping("/orders")
	public List<CustomerOrder> getOrders(@RequestParam("type") String type) throws JsonProcessingException {

		Iterable<EventStore> events = eventService.fetchAllEvents("ORDER");
		
		List<CustomerOrder> failureList = new ArrayList<>();
		Map<Long,CustomerOrder> orders = new HashMap<>();
			     
	    for (EventStore event : events) {
	    	
	    	CustomerOrder customerOrder = new Gson().fromJson(event.getEventData(), CustomerOrder.class);
	 
	        if (event.getEventType().equals("ORDER_ADDED")) {
	 
	        	orders.put(customerOrder.getOrderId(), customerOrder);
	            
	        } else if (event.getEventType().equals("ORDER_REMOVED")) {
	        	
	        	orders.remove(customerOrder.getOrderId());
	            failureList.add(customerOrder);
	        }
	    }
	    
	    if(type.equalsIgnoreCase("success")) {
	    	
	    	return orders.values().stream().collect(Collectors.toList());
	    }
	    else
	    	return failureList;
	}
	
	@GetMapping("/orders/history")
	public List<CustomerOrder> getOrdesUntilDate(@RequestParam("date") String date, @RequestParam("type") String type)	throws JsonProcessingException {

		String[] dateArray = date.split("-");

		LocalDateTime dateTill = LocalDate
				.of(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]))
				.atTime(23, 59);

		Iterable<EventStore> events = eventService.fetchAllEventsTillDate("ORDER", dateTill);

		List<CustomerOrder> failureList = new ArrayList<>();
		Map<Long, CustomerOrder> orders = new HashMap<>();

		for (EventStore event : events) {

			CustomerOrder customerOrder = new Gson().fromJson(event.getEventData(), CustomerOrder.class);

			if (event.getEventType().equals("ORDER_ADDED")) {

				orders.put(customerOrder.getOrderId(), customerOrder);

			} else if (event.getEventType().equals("ORDER_REMOVED")) {

				orders.remove(customerOrder.getOrderId());
				failureList.add(customerOrder);
			}
		}

		if (type.equalsIgnoreCase("success")) {

			return orders.values().stream().collect(Collectors.toList());
		} else
			return failureList;
	}
	
	@GetMapping("/orders/{orderId}")
	public CustomerOrder getOrdesById(@PathVariable Long orderId) throws JsonProcessingException {

		Iterable<EventStore> events = eventService.fetchByOrderId(orderId);
		CustomerOrder customerOrder = null;
		
		for (EventStore event : events) {
			if (event.getEventType().equals("ORDER_ADDED"))
				customerOrder = new Gson().fromJson(event.getEventData(), CustomerOrder.class);
			else if (event.getEventType().equals("ORDER_REMOVED"))
				customerOrder = new Gson().fromJson(event.getEventData(), CustomerOrder.class); 
		}

		return customerOrder;
	}

	@GetMapping("/stock")
	public Stock getstockOf(@RequestParam("name") String name) throws JsonProcessingException {

		Iterable<EventStore> events = eventService.fetchAllEvents("STOCK");
		
		Stock currentStock = new Stock();
		 
	    currentStock.setItem(name);
	    currentStock.setUser("NA");
	 
	    for (EventStore event : events) {
	 
	        Stock stock = new Gson().fromJson(event.getEventData(), Stock.class);
	 
	        if (stock.getItem().equals(name) && event.getEventType().equals("STOCK_ADDED")) {
	 
	            currentStock.setQuantity(currentStock.getQuantity() + stock.getQuantity());
	        } else if (stock.getItem().equals(name) && event.getEventType().equals("STOCK_REMOVED")) {
	 
	            currentStock.setQuantity(currentStock.getQuantity() - stock.getQuantity());
	        }
	    }
	 
	    return currentStock;
	}
	
	@GetMapping("/stocks")
	public List<Stock> getTotalstock() throws JsonProcessingException {

		Iterable<EventStore> events = eventService.fetchAllEvents("STOCK");
		
		Map<String,Stock> stockMap = new HashMap<>();
		
		Stock currentStock = null;

	    for (EventStore event : events) {
	 
	        Stock stock = new Gson().fromJson(event.getEventData(), Stock.class);
	        currentStock = new Stock();
	        
	        if (stockMap.containsKey(stock.getItem()) && event.getEventType().equals("STOCK_ADDED")) {
	        	
	        	currentStock.setItem(stock.getItem());
	            currentStock.setQuantity(stockMap.get(stock.getItem()).getQuantity() + stock.getQuantity());
	            
	        } else if (stockMap.containsKey(stock.getItem()) && event.getEventType().equals("STOCK_REMOVED")) {
	        	
	        	currentStock.setItem(stock.getItem());
	        	currentStock.setQuantity(stockMap.get(stock.getItem()).getQuantity() - stock.getQuantity());
	            
	        } else if(!stockMap.containsKey(stock.getItem())) {
	        	
	        	currentStock.setItem(stock.getItem());
	        	currentStock.setQuantity(stock.getQuantity());
	        }
	        
	        stockMap.put(stock.getItem(),currentStock);
	    }
	 
	    return stockMap.values().stream().collect(Collectors.toList());
	}
	

	@GetMapping("/stock/history")
	public Stock getStockUntilDate(@RequestParam("date") String date,@RequestParam("name") String name) throws JsonProcessingException {
	 
	     
	    String[] dateArray = date.split("-");
	     
	    LocalDateTime dateTill = LocalDate.of(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2])).atTime(23, 59);
	     
	     
	     
	    Iterable<EventStore> events = eventService.fetchAllEventsTillDate("STOCK",dateTill);
	 
	    Stock currentStock = new Stock();
	 
	    currentStock.setItem(name);
	    currentStock.setUser("NA");
	 
	    for (EventStore event : events) {
	 
	        Stock stock = new Gson().fromJson(event.getEventData(), Stock.class);
	 
	        if (stock.getItem().equals(name) && event.getEventType().equals("STOCK_ADDED")) {
	 
	            currentStock.setQuantity(currentStock.getQuantity() + stock.getQuantity());
	        } else if (stock.getItem().equals(name) && event.getEventType().equals("STOCK_REMOVED")) {
	 
	            currentStock.setQuantity(currentStock.getQuantity() - stock.getQuantity());
	        }
	    }
	 
	    return currentStock;
	 
	}

	
	@GetMapping("/shipments")
	public Integer getShipmentOf(@RequestParam("type") String type) throws JsonProcessingException {

		Iterable<EventStore> events = eventService.fetchAllEvents("SHIPMENT");

		Shipment returnshipment = new Shipment();
		int count = 0;

		for (EventStore event : events) {

			Shipment shipment = new Gson().fromJson(event.getEventData(), Shipment.class);

			if (event.getEventType().equals(type)) {

				count++;

			}

		}
			return count;
	}
	
	@GetMapping("/shipments/history")
	public Integer getShipmentUntilDate(@RequestParam("date") String date, @RequestParam("type") String type) throws JsonProcessingException {

		String[] dateArray = date.split("-");

		LocalDateTime dateTill = LocalDate
				.of(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]))
				.atTime(23, 59);

		Iterable<EventStore> events = eventService.fetchAllEventsTillDate("SHIPMENT", dateTill);

		Shipment returnshipment = new Shipment();
		int count = 0;

		for (EventStore event : events) {

			Shipment shipment = new Gson().fromJson(event.getEventData(), Shipment.class);

			if (event.getEventType().equals(type)) {

				count++;

			}

		}
		return count;
	}
}
