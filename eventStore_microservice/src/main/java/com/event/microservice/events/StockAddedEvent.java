package com.event.microservice.events;

import com.event.microservice.dto.Stock;
import com.event.microservice.interfaces.StockEvent;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StockAddedEvent implements StockEvent {
	
	private Stock stockDetails;
	
}
