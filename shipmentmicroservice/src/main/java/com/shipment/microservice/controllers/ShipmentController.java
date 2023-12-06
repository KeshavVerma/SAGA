package com.shipment.microservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shipment.microservice.dto.CustomerOrder;
import com.shipment.microservice.entities.Shipment;
import com.shipment.microservice.events.InventoryEvent;
import com.shipment.microservice.repositories.ShipmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@RefreshScope
@Controller
public class ShipmentController {
	
	Logger logger = LoggerFactory.getLogger("SHIPMENT-SERVICE");

    @Autowired
    private ShipmentRepository repository;

    @Autowired
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @KafkaListener(topics = "update-inventory", groupId = "inventory-group")
    public void shipOrder(String event) throws JsonMappingException, JsonProcessingException {
    	
    	logger.info("---In Shipment shipOrder Method---");

        Shipment shipment = new Shipment();
        InventoryEvent inventoryEvent = new ObjectMapper().readValue(event, InventoryEvent.class);
        CustomerOrder order = inventoryEvent.getOrder();
        try {

            if (order.getAddress() == null) {
                throw new Exception("Address not present");
            }

            shipment.setAddress(order.getAddress());
            shipment.setOrderId(order.getOrderId());

            shipment.setStatus("success");

            this.repository.save(shipment);

            // do other shipment logic ..

        } catch (Exception e) {
            shipment.setOrderId(order.getOrderId());
            shipment.setStatus("failed");
            this.repository.save(shipment);

            InventoryEvent reverseEvent = new InventoryEvent();

            reverseEvent.setType("INVENTORY_REVERSED");
            System.out.println(order);
            reverseEvent.setOrder(order);
            this.kafkaTemplate.send("reversed-inventory", reverseEvent);

        }
    }

}
