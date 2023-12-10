package com.event.microservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Payment {
	
	@JsonIgnore
	private String eventOf ="PAYMENT";

    private String mode;

    private Long orderId;

    private double amount;
    
    private String user;

}
