package com.inventory.microservice.controllers;

import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.microservice.dto.CustomerOrder;
import com.inventory.microservice.dto.Stock;
import com.inventory.microservice.entities.Inventory;
import com.inventory.microservice.events.InventoryEvent;
import com.inventory.microservice.events.PaymentEvent;
import com.inventory.microservice.repositories.InventoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class InventoryController {
	
	Logger logger = LoggerFactory.getLogger("INVENTORY-SERVICE");

    @Autowired
    private InventoryRepository repository;

    @Autowired
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaPaymentTemplate;

    @Transactional
    @KafkaListener(topics = "new-payments", groupId = "payments-group")
    public void updateInventory(String paymentEvent) throws JsonMappingException, JsonProcessingException {
    	
    	logger.info("---In Inventory updateInventory Method---");

        InventoryEvent event = new InventoryEvent();

        PaymentEvent p = new ObjectMapper().readValue(paymentEvent, PaymentEvent.class);
        CustomerOrder order = p.getOrder();

        try {

            // update stock in database
            Iterable<Inventory> inventories = this.repository.findByItem(order.getItem());

            boolean exists = inventories.iterator().hasNext();

            if (!exists)
                throw new Exception("Stock not available");

            inventories.forEach(
                    i -> {
                        i.setQuantity(i.getQuantity() - order.getQuantity());

                        this.repository.save(i);
                    });

            event.setType("INVENTORY_UPDATED");
            event.setOrder(order);
            this.kafkaTemplate.send("update-inventory", event);

        } catch (Exception e) {

            // reverse previous task
            PaymentEvent pe = new PaymentEvent();
            order.setFailIn("INVENTORY");
            pe.setOrder(order);
            pe.setType("PAYMENT_REVERSED");
            this.kafkaPaymentTemplate.send("reversed-payments", pe);

        }

    }

    @Transactional
    @PostMapping("/inventory")
    public void addInventory(@RequestBody Stock stock) {

        Iterable<Inventory> items = this.repository.findByItem(stock.getItem());
        InventoryEvent event = new InventoryEvent();

        if (items.iterator().hasNext()) {

            items.forEach(i -> {

                i.setQuantity(stock.getQuantity() + i.getQuantity());
                this.repository.save(i);
                
                event.setType("INVENTORY_UPDATED");
                event.setStock(stock);
                this.kafkaTemplate.send("update-inventory", event);
            });
        } else {

            Inventory i = new Inventory();
            i.setItem(stock.getItem());
            i.setQuantity(stock.getQuantity());
            this.repository.save(i);
            
            event.setType("INVENTORY_CREATED");
            event.setStock(stock);
            this.kafkaTemplate.send("new-inventory", event);
        }
    }

}
