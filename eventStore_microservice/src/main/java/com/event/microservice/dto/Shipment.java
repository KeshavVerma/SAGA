package com.event.microservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Shipment {

	@JsonIgnore
	private String eventOf = "SHIPMENT";
	private String address;
	private long orderId;

}
