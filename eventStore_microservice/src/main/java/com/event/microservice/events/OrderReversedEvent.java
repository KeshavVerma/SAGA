package com.event.microservice.events;

import com.event.microservice.dto.CustomerOrder;
import com.event.microservice.dto.Stock;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderReversedEvent {

	private CustomerOrder orderDetails;
	
}
