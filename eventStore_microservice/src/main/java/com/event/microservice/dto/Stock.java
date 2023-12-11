package com.event.microservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Stock {
	
	@JsonIgnore
	private String eventOf ="STOCK";

	private String item;

	private int quantity;
	
	private long orderId;

	private String user;
	
	

}
