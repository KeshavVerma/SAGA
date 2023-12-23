package com.webclient.controllers;

import java.util.List;

import com.webclient.dto.CustomerOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webclient")
public class WebclientController {
	
	@Autowired
	private WebClient webClient;
	
	@Autowired
	private WebClient.Builder webClientBuilder;

	@GetMapping("/orders")
	public ResponseEntity<?> getOrders(@RequestParam String type) {
		// not working below due to array coming in response json
//		CustomerOrder orders = webClient.get().uri("/orders?type=Success").retrieve().bodyToMono(CustomerOrder.class).block();	
//		System.out.println(orders);
		
		// this is working by full URL with host name and port number
		//Mono<List<CustomerOrder>> orders = webClient.get().uri("/orders?type="+type).retrieve().bodyToMono(new ParameterizedTypeReference<List<CustomerOrder>>(){});
		
		// this is working by Service Name
		Mono<List<CustomerOrder>> orders = webClientBuilder.build().get().uri("/orders?type="+type).retrieve().bodyToMono(new ParameterizedTypeReference<List<CustomerOrder>>(){});
		List<CustomerOrder> orderList = orders.block();
//		System.out.println(orderList);
		// if want in Array
//		Mono<CustomerOrder[]> orders = webClient.get().uri("/orders?type=Success").retrieve().bodyToMono(CustomerOrder[].class);
//			CustomerOrder[] orderArray = orders.block();
		return new ResponseEntity<>(orderList,HttpStatus.OK);
	}
}
