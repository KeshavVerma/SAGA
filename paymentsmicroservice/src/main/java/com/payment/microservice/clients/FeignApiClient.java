package com.payment.microservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="CREDITCARD-SERVICE")
public interface FeignApiClient {
	
	@GetMapping("/cards/payments/{cardType}")
	public boolean processCard(@PathVariable String cardType);

}
