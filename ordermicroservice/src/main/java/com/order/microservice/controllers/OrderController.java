package com.order.microservice.controllers;

import java.util.List;
import java.util.Optional;

import com.order.microservice.dto.CustomerOrder;
import com.order.microservice.entities.OrderEntity;
import com.order.microservice.events.OrderEvent;
import com.order.microservice.repositories.OrderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
	
	Logger logger = LoggerFactory.getLogger("ORDER-SERVICE");

    @Autowired
    private OrderRepository repository;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @PostMapping("/orders")
    public void createOrder(@RequestBody CustomerOrder customerOrder) {

    	logger.info("---In Order createOrder Method---");
    	
        OrderEntity order = new OrderEntity();
        try {
            // save order in database

            order.setAmount(customerOrder.getAmount());
            order.setItem(customerOrder.getItem());
            order.setQuantity(customerOrder.getQuantity());
            order.setStatus("CREATED");
            order = this.repository.save(order);

            customerOrder.setOrderId(order.getId());

            // publish order created event for payment microservice to consume.

            OrderEvent event = new OrderEvent();
            event.setOrder(customerOrder);
            event.setType("ORDER_CREATED");
            this.kafkaTemplate.send("new-orders", event);
        } catch (Exception e) {

            order.setStatus("FAILED");
            this.repository.save(order);

        }

    }
    
	@GetMapping("/test")
	public String test() {
		return "This is Test";
	}

}
