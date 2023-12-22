package com.payment.microservice.controllers;

import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.microservice.clients.FeignApiClient;
import com.payment.microservice.dto.CustomerOrder;
import com.payment.microservice.entities.Payment;
import com.payment.microservice.events.OrderEvent;
import com.payment.microservice.events.PaymentEvent;
import com.payment.microservice.repositories.PaymentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@RefreshScope
@Controller
public class PaymentController {
	
	Logger logger = LoggerFactory.getLogger("PAYMENT-SERVICE");

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaOrderTemplate;
    
    @Autowired
    private FeignApiClient feignApiClient;

    @Transactional
    @KafkaListener(topics = "new-orders", groupId = "orders-group")
    public void processPayment(String event) throws JsonMappingException, JsonProcessingException {

    	logger.info("---In Payment processPayment Method---");
    	
    	System.out.println("Recieved event" + event);
        OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);

        CustomerOrder order = orderEvent.getOrder();
        Payment payment = new Payment();
        try {

			// save payment details in db
			payment.setAmount(order.getAmount());
			payment.setMode(order.getPaymentMode());
			payment.setOrderId(order.getOrderId());
			// call card service for payment
			boolean paymentStatus = feignApiClient.processCard(order.getPaymentMode());
			System.out.println("************"+paymentStatus);
			if (paymentStatus) {
				payment.setStatus("SUCCESS");
				payment = this.repository.save(payment);
				order.setPaymentId(payment.getId());
				// publish payment created event for inventory microservice to consume.

				PaymentEvent paymentEvent = new PaymentEvent();
				paymentEvent.setOrder(order);
				paymentEvent.setType("PAYMENT_CREATED");
				this.kafkaTemplate.send("new-payments", paymentEvent);
			} else {
				logger.error("--Payment Fail with status "+paymentStatus);
				throw new Exception("Payment Fail");
			}
            
        } catch (Exception e) {
        	
        	logger.error("--In Payment Exception "+e.getMessage());
            payment.setOrderId(order.getOrderId());
            payment.setStatus("FAILED");
            repository.save(payment);

            // reverse previous task
            OrderEvent oe = new OrderEvent();
            order.setFailIn("PAYMENT");
            oe.setOrder(order);
            oe.setType("ORDER_REVERSED");
            this.kafkaOrderTemplate.send("reversed-orders", orderEvent);

        }

    }

}
