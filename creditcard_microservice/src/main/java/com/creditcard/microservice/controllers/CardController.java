package com.creditcard.microservice.controllers;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/cards")
public class CardController {
	
	Logger logger = LoggerFactory.getLogger("CREDITCARD-SERVICE");
	
	
	@GetMapping("/payments/{cardType}")
	public boolean processCard(@PathVariable  String cardType) {
		
		logger.info("---In cards Payment processCard---");
		boolean result=false;
		if(cardType.equals("CreditCard"))
			result= new Random().nextBoolean()?true:false;
		
		return result;
	}

}
